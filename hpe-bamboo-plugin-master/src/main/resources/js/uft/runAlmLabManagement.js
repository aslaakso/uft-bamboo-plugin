var customWidth = "500px";
document.getElementById('almServer').style.maxWidth=customWidth;
document.getElementById('userName').style.maxWidth=customWidth;
document.getElementById('password').style.maxWidth=customWidth;
document.getElementById('domain').style.maxWidth=customWidth;
document.getElementById('projectName').style.maxWidth=customWidth;
document.getElementById('runType').style.maxWidth=customWidth;
document.getElementById('testId').style.maxWidth=customWidth;
document.getElementById('description').style.maxWidth=customWidth;
document.getElementById('duration').style.maxWidth=customWidth;
document.getElementById('environmentId').style.maxWidth=customWidth;
document.getElementById('deployedEnvironmentName').style.maxWidth=customWidth;
document.getElementById('deploymentAction').style.maxWidth=customWidth;
document.getElementById('deprovisioningAction').style.maxWidth=customWidth;

function toggle_visibility(id) {
    var e = document.getElementById(id);
    if(e.style.display == 'block')
        e.style.display = 'none';
    else
        e.style.display = 'block';
}
