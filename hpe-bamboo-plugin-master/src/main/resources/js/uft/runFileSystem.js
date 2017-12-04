var jobId,
    wizard,
    loginInfo,
    mcServerURLInput,
    mcUserNameInput,
    mcPasswordInput,
    useProxy,
    proxyAddress,
    proxyUserName,
    proxyPassword,
    useAuthentication;
var customWidth = "500px";
document.getElementById('timeoutInput').style.maxWidth=customWidth;
document.getElementById('testPathInput').style.maxWidth=customWidth;
document.getElementById('publishMode').style.maxWidth=customWidth;
document.getElementById('mcServerURLInput').style.maxWidth=customWidth;
document.getElementById('mcUserNameInput').style.maxWidth=customWidth;
document.getElementById('mcPasswordInput').style.maxWidth=customWidth;
document.getElementById('extraApps').style.maxWidth=customWidth;
var openMCBtn = document.getElementById('openMCBtn');
var specifyAuthenticationBox = document.getElementById('specifyAuthentication');
specifyAuthenticationBox.addEventListener('change', function (e) {
    var proxyUserNameInput = document.getElementById('proxyUserName'),
        proxyPasswordInput = document.getElementById('proxyPassword');

    if (specifyAuthenticationBox.checked == true) {
        proxyUserNameInput.disabled = false;
        proxyPasswordInput.disabled = false;
    } else {
        proxyUserNameInput.disabled = true;
        proxyPasswordInput.disabled = true;
    }
});

function toggle_visibility(id) {
    var e = document.getElementById(id);
    if (e.style.display == 'block')
        e.style.display = 'none';
    else
        e.style.display = 'block';
}

function openMCWizardHandler(e) {
    //disable open wizard button
    openMCBtn.disabled = true;

    //get login info, url, username, password
    mcServerURLInput = document.getElementById('mcServerURLInput').value.replace(/\/$/, "");//remove tailing slash
    mcUserNameInput = document.getElementById('mcUserNameInput').value;
    mcPasswordInput = document.getElementById('mcPasswordInput').value;
    proxyAddress = document.getElementById('proxyAddress').value;
    proxyUserName = document.getElementById('proxyUserName').value;
    proxyPassword = document.getElementById('proxyPassword').value;
    useProxy = document.getElementById('useProxy').checked;
    useAuthentication = specifyAuthenticationBox.checked;

    loginInfo = {
        mcServerURLInput: mcServerURLInput,
        mcUserNameInput: mcUserNameInput,
        mcPasswordInput: mcPasswordInput,
        proxyAddress: proxyAddress,
        proxyUserName: proxyUserName,
        proxyPassword: proxyPassword,
        useProxy: useProxy,
        useAuthentication: useAuthentication
    };
    //no need do login, get job id directly
    getJobIdHelper();
}

function getJobIdHelper() {
    var jobIdInput = document.getElementById("jobUUID");
    AJS.$.ajax({
        url: "${req.contextPath}/plugins/servlet/httpOperationServlet?method=createTempJob",
        type: "POST",
        data: loginInfo,
        dataType: "json",
        success: function(data) {
            //data = JSON.parse(data);
            if(data != null){
                var errorCode = data.myErrorCode;

                if (errorCode != null) {
                    openMCBtn.disabled = false;
                    if (errorCode == 0) {
                        alert("The URL, User name, and Password fields cannot be empty.");
                        return;
                    } else if (errorCode == 2) {
                        alert("Use Proxy is enabled, but no proxy address was provided.");
                        return;
                    } else if (errorCode == 4) {
                        alert("Specific Authentication is selected, but the Proxy User name or password is empty.");
                        return;
                    }
                }
                jobId =  data.data && data.data.id;

                if (!jobId){
                    alert('The login to Mobile Center failed. Check that the Mobile Center login information is correct.');
                    openMCBtn.disabled = false;
                    return;
                }

                //set jobId to hidden input
                jobIdInput.value = jobId;
                //open MC wizard
                wizard = window.open(
                    mcServerURLInput+ "/integration/#/login?jobId=" + jobId + "&displayUFTMode=true&appType=native",
                    "MCWizardWindow",
                    "width=1024,height=768");
                wizard.focus();
                window.addEventListener('message', messageEventHandler, false);
            }else{
                alert('The login to Mobile Center failed. Check that the Mobile Center login information is correct.');
                openMCBtn.disabled = false;
                return;
            }


        },
        error: function(error) {
            var errorCode = error.myErrorCode;
            if (errorCode == 0) {
                alert("The URL, User name, and Password fields cannot be empty.");
            } else if (errorCode == 2) {
                alert("Use Proxy is enabled, but no proxy address was provided.");
            } else if (errorCode == 4) {
                alert("Specific Authentication is selected, but the Proxy User name or password is empty.");
            }

            openMCBtn.disabled = false;
        }
    });
}

function messageEventHandler(event) {
    var me = this;
    //stop event bubble
    event.stopPropagation();
    console.log("===message event listener called from bamboo=====", event.data);

    if (event && event.data == "mcJobUpdated") {
        console.log("=====get device and application from mc success====", loginInfo);
        //get device and application
        AJS.$.ajax({
            url: "${req.contextPath}/plugins/servlet/httpOperationServlet?method=getJobJSONData&jobUUID=" + jobId,
            type: "POST",
            data: loginInfo,
            dataType: "json",
            success: function(data) {
                //data = JSON.parse(data);
                console.log("=====get device and application from mc success====", data);
                //set device and application information to test
                me._parseTestInfoHelper(data);
                //enable action button after the wizard closed
                openMCBtn.disabled = false;

                wizard.close();
            },
            error: function(error) {
                console.log("=====get job detail from mc fail====");
                alert('Get job detail information from Mobile Center failed, please try again.');
                //enable action button after the wizard closed
                openMCBtn.disabled = false;
            }
        });
    }

    if (event && event.data === 'mcCloseWizard') {
        wizard.close();
        //enable action button after the wizard closed
        openMCBtn.disabled = false;
    }
}

function checkWizardStatus() {
    if (wizard && wizard.closed) {
        clearInterval(timer);
        //enable action button after the wizard closed
        openMCBtn.disabled = false;
    }
}
var timer = setInterval(checkWizardStatus, 500);

function _parseTestInfoHelper(testData) {
    delete testData.jobUUID;
    //render extra apps first
    _extraAppsReader(testData.extraApps || []);

    //delete testData.extraApps;

    for (var key in testData) {
        if (key == 'extraApps') continue;

        for (var infoKey in testData[key]) {
            if (document.getElementById(infoKey) != null) {
                document.getElementById(infoKey).value = testData[key][infoKey];
            }
        }
    }

    //deviceCapability and specificDevice cannot exist at the same time
    if (testData.specificDevice.deviceId) {
        document.getElementById('targetLab').value = '';
    } else {
        document.getElementById('deviceId').value = '';
    }

    return false;
}

function _extraAppsReader(extraApps) {
    console.log('=====extraApps=====', extraApps);
    var extraAppsContainer = document.getElementById("extraApps");
    var extraAppsInfo = '';

    //remove all children before add new
    //extraAppsContainer.innerHTML = '';
    extraApps.forEach(function(app, index, array) {
        // extraAppsContainer.appendChild(appContainer);
        extraAppsInfo += app.extraAppName + ': ' + app.instrumented + '\n';
    });

    extraAppsContainer.value = extraAppsInfo;

    return false;
}
