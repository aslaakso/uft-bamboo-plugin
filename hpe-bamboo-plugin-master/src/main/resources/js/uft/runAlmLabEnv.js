var customWidth = "500px";
document.getElementById('almServer').style.maxWidth=customWidth;
document.getElementById('almUserName').style.maxWidth=customWidth;
document.getElementById('almUserPassword').style.maxWidth=customWidth;
document.getElementById('domain').style.maxWidth=customWidth;
document.getElementById('almProject').style.maxWidth=customWidth;
document.getElementById('AUTEnvID').style.maxWidth=customWidth;
document.getElementById('AUTConfName').style.maxWidth=customWidth;
document.getElementById('pathToJSONFile').style.maxWidth=customWidth;
document.getElementById('NewAUTConfName').style.maxWidth=customWidth;
document.getElementById('outEnvID').style.maxWidth=customWidth;
document.getElementById('almParamTypes').style.maxWidth=customWidth;
document.getElementById('almParamName').style.maxWidth=customWidth;
document.getElementById('almParamValue').style.maxWidth=customWidth;

function addNewALMParam() {
    var divTemplate = document.getElementById('ParamTemplate');
    var table = document.getElementById('paramTable');

    var row = document.createElement("TR");
    var td1 = document.createElement("TD");
    var td2 = document.createElement("TD");

    var strHtml5 = "<INPUT TYPE=\"Button\" CLASS=\"aui-button aui-button-primary\" onClick=\"javascript: delRow(this)\" VALUE=\"[@ww.text name='AlmLabEnvPrepareTask.btn.Delete'/]\">";
    td1.innerHTML = strHtml5;

    var divClone = divTemplate.cloneNode(true);
    td2.appendChild(divClone);

    row.appendChild(td1);
    row.appendChild(td2);

    table.appendChild(row);
}

function delRow(tableID) {
    var current = tableID;
    while ( (current = current.parentElement)  && current.tagName !="TR");
    current.parentElement.removeChild(current);
}

function toggle_visibility(id) {
    var e = document.getElementById(id);
    if(e.style.display == 'block')
        e.style.display = 'none';
    else
        e.style.display = 'block';
}