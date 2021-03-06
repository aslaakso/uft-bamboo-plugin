package com.adm.bamboo.plugin.uft.ui;

import com.adm.bamboo.plugin.uft.api.AbstractLauncherTaskConfigurator;
import com.adm.tools.common.model.CdaDetails;
import com.adm.tools.common.model.EnumDescription;
import com.adm.tools.common.model.SseModel;
import com.adm.utils.uft.enums.UFTConstants;
import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.utils.i18n.I18nBean;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlmLabManagementUftTaskConfigurator extends AbstractLauncherTaskConfigurator {

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition) {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put(UFTConstants.ALM_SERVER.getValue(), params.getString(UFTConstants.ALM_SERVER.getValue()));
        config.put(UFTConstants.USER_NAME.getValue(), params.getString(UFTConstants.USER_NAME.getValue()));
        config.put(UFTConstants.PASSWORD.getValue(), params.getString(UFTConstants.PASSWORD.getValue()));
        config.put(UFTConstants.DOMAIN_PARAM.getValue(), params.getString(UFTConstants.DOMAIN_PARAM.getValue()));
        config.put(UFTConstants.PROJECT_NAME_PARAM.getValue(), params.getString(UFTConstants.PROJECT_NAME_PARAM.getValue()));
        config.put(UFTConstants.RUN_TYPE_PARAM.getValue(), params.getString(UFTConstants.RUN_TYPE_PARAM.getValue()));
        config.put(UFTConstants.TEST_ID_PARAM.getValue(), params.getString(UFTConstants.TEST_ID_PARAM.getValue()));
        config.put(UFTConstants.DESCRIPTION_PARAM.getValue(), params.getString(UFTConstants.DESCRIPTION_PARAM.getValue()));
        config.put(UFTConstants.DURATION_PARAM.getValue(), params.getString(UFTConstants.DURATION_PARAM.getValue()));
        config.put(UFTConstants.ENVIRONMENT_ID_PARAM.getValue(), params.getString(UFTConstants.ENVIRONMENT_ID_PARAM.getValue()));
        config.put(UFTConstants.USE_SDA_PARAM.getValue(), params.getString(UFTConstants.USE_SDA_PARAM.getValue()));
        config.put(UFTConstants.DEPLOYMENT_ACTION_PARAM.getValue(), params.getString((UFTConstants.DEPLOYMENT_ACTION_PARAM.getValue())));
        config.put(UFTConstants.DEPLOYED_ENVIRONMENT_NAME.getValue(), params.getString((UFTConstants.DEPLOYED_ENVIRONMENT_NAME.getValue())));
        config.put(UFTConstants.DEPROVISIONING_ACTION_PARAM.getValue(), params.getString((UFTConstants.DEPROVISIONING_ACTION_PARAM.getValue())));
        config.put(UFTConstants.TASK_NAME.getValue(), getI18nBean().getText(UFTConstants.ALM_LAB_TASK_NAME.getValue()));

        return config;
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
        super.validate(params, errorCollection);

        I18nBean textProvider = getI18nBean();

        String almServer = params.getString(UFTConstants.ALM_SERVER.getValue());
        if (StringUtils.isEmpty(almServer)) {
            errorCollection.addError(UFTConstants.ALM_SERVER.getValue(), textProvider.getText(UFTConstants.ALM_SERVER_REQUIRED_STRING.getValue()));
        }

        String userName = params.getString(UFTConstants.USER_NAME.getValue());
        if (StringUtils.isEmpty(userName)) {
            errorCollection.addError(UFTConstants.USER_NAME.getValue(), textProvider.getText(UFTConstants.USER_NAME_REQUIRED_STRING.getValue()));
        }

        String domain = params.getString(UFTConstants.DOMAIN.getValue());
        if (StringUtils.isEmpty(domain)) {
            errorCollection.addError(UFTConstants.DOMAIN.getValue(), textProvider.getText(UFTConstants.DOMAIN_REQUIRED_STRING.getValue()));
        }

        String projectName = params.getString(UFTConstants.PROJECT_NAME_PARAM.getValue());
        if (StringUtils.isEmpty(projectName)) {
            errorCollection.addError(UFTConstants.PROJECT_NAME_PARAM.getValue(), textProvider.getText(UFTConstants.PROJECT_NAME_REQUIRED_STRING.getValue()));
        }

        String testId = params.getString(UFTConstants.TEST_ID_PARAM.getValue());
        if (StringUtils.isEmpty(testId)) {
            errorCollection.addError(UFTConstants.TEST_ID_PARAM.getValue(), textProvider.getText(UFTConstants.TEST_ID_REQUIRED_STRING.getValue()));
        }

        String duration = params.getString(UFTConstants.DURATION_PARAM.getValue());
        if (StringUtils.isEmpty(duration)) {
            errorCollection.addError(UFTConstants.DURATION_PARAM.getValue(), textProvider.getText(UFTConstants.DURATION_REQUIRED_STRING.getValue()));
        } else {
            try {
                int durationInt = Integer.parseInt(duration);
                if (durationInt < 30) {
                    errorCollection.addError(UFTConstants.DURATION_PARAM.getValue(), textProvider.getText(UFTConstants.DURATION_MINIMUM_STRING.getValue()));
                }
            } catch (NumberFormatException ex) {
                errorCollection.addError(UFTConstants.DURATION_PARAM.getValue(), textProvider.getText(UFTConstants.DURATION_INVALID_FORMAT_STRING.getValue()));
            }
        }
    }

    @Override
    public void populateContextForCreate(@NotNull final java.util.Map<String, Object> context) {
        super.populateContextForCreate(context);

        populateContextForLists(context);
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) {
        super.populateContextForEdit(context, taskDefinition);

        context.put(UFTConstants.ALM_SERVER.getValue(), taskDefinition.getConfiguration().get(UFTConstants.ALM_SERVER.getValue()));
        context.put(UFTConstants.USER_NAME.getValue(), taskDefinition.getConfiguration().get(UFTConstants.USER_NAME.getValue()));
        context.put(UFTConstants.PASSWORD.getValue(), taskDefinition.getConfiguration().get(UFTConstants.PASSWORD.getValue()));
        context.put(UFTConstants.DOMAIN.getValue(), taskDefinition.getConfiguration().get(UFTConstants.DOMAIN.getValue()));
        context.put(UFTConstants.PROJECT_NAME_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.PROJECT_NAME_PARAM.getValue()));
        context.put(UFTConstants.RUN_TYPE_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.RUN_TYPE_PARAM.getValue()));
        context.put(UFTConstants.TEST_ID_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.TEST_ID_PARAM.getValue()));
        context.put(UFTConstants.DESCRIPTION_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.DESCRIPTION_PARAM.getValue()));
        context.put(UFTConstants.DURATION_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.DURATION_PARAM.getValue()));
        context.put(UFTConstants.ENVIRONMENT_ID_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.ENVIRONMENT_ID_PARAM.getValue()));
        context.put(UFTConstants.USE_SDA_PARAM.getValue(), taskDefinition.getConfiguration().get(UFTConstants.USE_SDA_PARAM.getValue()));
        context.put(UFTConstants.DEPLOYMENT_ACTION_PARAM.getValue(), taskDefinition.getConfiguration().get((UFTConstants.DEPLOYMENT_ACTION_PARAM.getValue())));
        context.put(UFTConstants.DEPLOYED_ENVIRONMENT_NAME.getValue(), taskDefinition.getConfiguration().get((UFTConstants.DEPLOYED_ENVIRONMENT_NAME.getValue())));
        context.put(UFTConstants.DEPROVISIONING_ACTION_PARAM.getValue(), taskDefinition.getConfiguration().get((UFTConstants.DEPROVISIONING_ACTION_PARAM.getValue())));

        populateContextForLists(context);
    }

    private void populateContextForLists(@NotNull final Map<String, Object> context) {
        context.put(UFTConstants.RUN_TYPE_ITEMS_PARAM.getValue(), getRunTypes());
        context.put(UFTConstants.DEPLOYMENT_ACTION_ITEMS_PARAM.getValue(), getActions(CdaDetails.getDeploymentActions()));
        context.put(UFTConstants.DEPROVISIONING_ACTION_ITEMS_PARAM.getValue(), getActions(CdaDetails.getDeprovisioningActions()));
    }

    private Map<String, String> getRunTypes() {
        Map<String, String> runTypesMap = new HashMap<String, String>();

        for (EnumDescription description : SseModel.getRunTypes()) {
            runTypesMap.put(description.getValue(), description.getDescription());
        }

        return runTypesMap;
    }

    /**
     * Get deployment/deprovisioning actions
     *
     * @param actions
     * @return actions map
     */
    private Map<String, String> getActions(final List<EnumDescription> actions) {
        Map<String, String> actionsMap = new HashMap<String, String>();

        for (EnumDescription description : actions) {
            actionsMap.put(description.getValue(), description.getDescription());
        }

        return actionsMap;
    }
}
