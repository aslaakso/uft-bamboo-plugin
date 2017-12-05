package com.adm.bamboo.plugin.uft.task;

import com.adm.bamboo.plugin.uft.api.AbstractLauncherTask;
import com.adm.utils.uft.enums.UFTConstants;
import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.utils.i18n.I18nBean;
import com.atlassian.bamboo.utils.i18n.I18nBeanFactory;
import com.hpe.application.automation.tools.common.SSEException;
import com.hpe.application.automation.tools.common.model.CdaDetails;
import com.hpe.application.automation.tools.common.rest.RestClient;
import com.hpe.application.automation.tools.common.result.ResultSerializer;
import com.hpe.application.automation.tools.common.result.model.junit.Testsuites;
import com.hpe.application.automation.tools.common.sdk.Args;
import com.hpe.application.automation.tools.common.sdk.Logger;
import com.hpe.application.automation.tools.common.sdk.RunManager;
import org.apache.commons.lang.BooleanUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

import static com.adm.bamboo.plugin.uft.results.TestResultHelper.collateTestResults;
import static com.adm.bamboo.plugin.uft.results.TestResultHelperAlm.AddALMArtifacts;

public class RunFromAlmLabManagementUftTask implements AbstractLauncherTask {
    private final TestCollationService testCollationService;
    private static I18nBean i18nBean;

    private final String LINK_SEARCH_FILTER = "run report for run id";

    public RunFromAlmLabManagementUftTask(final TestCollationService testCollationService, @NotNull final I18nBeanFactory i18nBeanFactory) {
        this.testCollationService = testCollationService;
        i18nBean = i18nBeanFactory.getI18nBean();
    }

    @NotNull
    @Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        final ConfigurationMap map = taskContext.getConfigurationMap();

        final String almServerPath = map.get(UFTConstants.ALM_SERVER.getValue());

        RunManager runManager = new RunManager();

        CdaDetails cdaDetails = null;
        boolean useCda = BooleanUtils.toBoolean(map.get(UFTConstants.USE_SDA_PARAM.getValue()));
        if (useCda) {
            cdaDetails = new CdaDetails(map.get(UFTConstants.DEPLOYMENT_ACTION_PARAM.getValue()),
                    map.get(UFTConstants.DEPLOYED_ENVIRONMENT_NAME.getValue()),
                    map.get(UFTConstants.DEPROVISIONING_ACTION_PARAM.getValue()));
        }

        buildLogger.addBuildLogEntry("cdaDetails" + cdaDetails);

        Args args = new Args(
                almServerPath,
                map.get(UFTConstants.DOMAIN_PARAM.getValue()),
                map.get(UFTConstants.PROJECT_NAME_PARAM.getValue()),
                map.get(UFTConstants.USER_NAME.getValue()),
                map.get(UFTConstants.PASSWORD.getValue()),
                map.get(UFTConstants.RUN_TYPE_PARAM.getValue()),
                map.get(UFTConstants.TEST_ID_PARAM.getValue()),
                map.get(UFTConstants.DURATION_PARAM.getValue()),
                map.get(UFTConstants.DESCRIPTION_PARAM.getValue()),
                null,
                map.get(UFTConstants.ENVIRONMENT_ID_PARAM.getValue()),
                cdaDetails);

        RestClient restClient =
                new RestClient(
                        args.getUrl(),
                        args.getDomain(),
                        args.getProject(),
                        args.getUsername());

        try {
            Logger logger = new Logger() {

                public void log(String message) {
                    buildLogger.addBuildLogEntry(message);
                }
            };
            buildLogger.addBuildLogEntry("args: " + args);

            //run task
            Testsuites result = runManager.execute(restClient, args, logger);

            buildLogger.addBuildLogEntry("test suite result: " + result);
            ResultSerializer.saveResults(result, taskContext.getWorkingDirectory().getPath(), logger);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return TaskResultBuilder.newBuilder(taskContext).failed().build();
        } catch (SSEException e) {
            return TaskResultBuilder.newBuilder(taskContext).failed().build();
        }

        return collateResults(taskContext, null);
    }

    @Override
    public Properties getTaskProperties(TaskContext taskContext) throws Exception {
        return null;
    }

    @Override
    public TaskResult collateResults(@NotNull TaskContext taskContext, Properties mergedProperties) {
        try {
            collateTestResults(testCollationService, taskContext);
            AddALMArtifacts(taskContext, null, LINK_SEARCH_FILTER, i18nBean);
            return TaskResultBuilder.newBuilder(taskContext).checkTestFailures().build();
        } catch (Exception ex) {
            return TaskResultBuilder.newBuilder(taskContext).failed().build();
        }
    }

}