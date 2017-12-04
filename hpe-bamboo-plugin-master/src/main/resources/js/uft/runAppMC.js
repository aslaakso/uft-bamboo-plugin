var specifyAuthenticationBox = document.getElementById('specifyAuthentication');
specifyAuthenticationBox.addEventListener('change', function(e) {
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
function addnewMCParam() {
    var divTemplate = document.getElementById('ParamTemplate');
    var table = document.getElementById('paramTable');

    var row = document.createElement("TR");
    var td1 = document.createElement("TD");
    var td2 = document.createElement("TD");

    var strHtml5 = "<INPUT TYPE=\"Button\" CLASS=\"aui-button aui-button-primary\" onClick=\"javascript: delRow(this)\" VALUE=\"[@ww.text name='Delete'/]\">";
    td1.innerHTML = strHtml5;
    td1.width = '100px';
    td1.style.paddingTop = '20px';

    var divClone = divTemplate.cloneNode(true);
    td2.appendChild(divClone);

    row.appendChild(td1);
    row.appendChild(td2);

    table.appendChild(row);
}

function delRow(tableID) {
    var current = tableID;
    while ((current = current.parentElement) && current.tagName != "TR");
    current.parentElement.removeChild(current);
}
