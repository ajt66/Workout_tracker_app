// Exercise page ------------------------------------------------------------------------------------------------------
var coll = document.getElementsByClassName("collapsible");
var i;

for (i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function () {
        this.classList.toggle("active");
        var content = this.nextElementSibling;
        if (content.style.maxHeight) {
            content.style.maxHeight = null;
            content.style.visibility = "hidden";
        } else {
            content.style.maxHeight = content.scrollHeight + "px";
            content.style.visibility = "visible";
        }
    });
}

// General -------------------------------------------------------------------------------------------------------------
function openNav() {
    document.getElementById("sidenav").classList.add("open");
}

function closeNav() {
    document.getElementById("sidenav").classList.remove("open");
}

function openpopup(popupname) {
    var i, popup;

    popup = document.getElementsByClassName("popup");
    for (i = 0; i < popup.length; i++) {
        popup[i].style.display = "none";
    }

    document.getElementById(popupname).style.display = "block";
}

// Analyze page ----------------------------------------------------------------------------------------------------------
window.onload = function () {
    const activeTab = localStorage.getItem('activeTab');
    if (activeTab) {
        document.getElementById(activeTab).click();
    } else {
        document.getElementById("metrics").click();
    }
};

function opentab(evt, tabName) {
    var i, tabcontent, tablinks;

    tabcontent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    tablinks = document.getElementsByClassName("tablink");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" selected", "");
    }

    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " selected";
    localStorage.setItem('activeTab', evt.currentTarget.id);
}

// daily-log page -------------------------------------------------------------------------------------------------------
document.querySelector("#date-select").valueAsDate = new Date();

function hideCard() {
    document.getElementById("dlcol-card").style.display = "none";
}