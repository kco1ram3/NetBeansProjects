var display_photo = document.getElementById("display_photo");
var display_video = document.getElementById("display_video");
var display_map = document.getElementById("display_map");
var commentMessage = document.getElementById("commentMessage");
var imgLoadingComment = document.getElementById("imgLoadingComment");
var btnLoadComment = document.getElementById("btnLoadComment");
var numOfComment = document.getElementById("numOfComment");
var pageSize = document.getElementById("pageSize");
var currentSize = document.getElementById("currentSize");
var latitudeValue = document.getElementById("latitudeValue");
var longitudeValue = document.getElementById("longitudeValue");
var gmap = document.getElementById("gmap");
var map;
var marker;
var videoPlayer, btnRestart, btnPlay, btnStop, btnBackward, btnForward, btnFullscreen, seekSliderTime, currentTime, durationTime;

function checkComment() {
    var isValid = true;
    if (commentMessage.value === "") {
        commentMessage.className = "invalid_textbox";
        isValid = false;
    } else {
        numOfComment.value = parseInt(numOfComment.value) + 1;
        commentMessage.className = commentMessage.className.replace("invalid_textbox", "");
    }
    return isValid;
    //document.forms['login'].submit();
}

function setCurrentSize(isPostback) {
    if (typeof isPostback == "undefined" || !isPostback) {
        currentSize.value = parseInt(pageSize.value);
    } else {
        currentSize.value = parseInt(currentSize.value) + parseInt(pageSize.value);
    }
}

function loadComment(IsLoadAddition) {
    var urlComment = document.getElementById("urlComment");
    var rowID = document.getElementById("rowID");
    var data = "";
    data += "rowID_T_Photo=" + rowID.value;
    data += "&size=" + parseInt(currentSize.value);
    if (IsLoadAddition) {
        imgLoadingComment.style.display = "";
        btnLoadComment.style.display = "none";
        /*
        setTimeout(function() {
            ajaxLoad("get", "${urlComment}", data, "comment_detail", "loadCommentComplete");
        }, 1000);
        */
        ajaxLoad("get", urlComment.value, data, "comment_detail", "loadCommentComplete");
    } else {
        ajaxLoad("get", urlComment.value, data, "comment_detail", "loadCommentComplete");
    }
}

function loadCommentComplete() {
    imgLoadingComment.style.display = "none";
    if (parseInt(currentSize.value) < parseInt(numOfComment.value)) {
        btnLoadComment.style.display = "";
    }
}

function changeDisplay(displayType) {
    if (displayType == 1) {
        display_photo.style.display = "";
        display_video.style.display = "none";
        display_map.style.display = "none";
    } else if (displayType == 2) {
        display_photo.style.display = "none";
        display_video.style.display = "";
        display_map.style.display = "none";
    } else {
        display_photo.style.display = "none";
        display_video.style.display = "none";
        display_map.style.display = "";
        populateMap();
    }
}

window.onload = function() {
    btnLoadComment.style.display = "none";
    setCurrentSize();
    loadComment(true);
    changeDisplay(1);
    intializePlayer();
};

function populateMap() {
    geocoder = new google.maps.Geocoder();
    var mapOptions = {
        zoom: 15,
        center: new google.maps.LatLng(13.7278956, 100.52412349999997),
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);

    if ((latitudeValue.value != "" && longitudeValue.value != "") && (latitudeValue.value != "0" && longitudeValue.value != "0")) {
        marker = new google.maps.Marker({
            map: map,
            position: new google.maps.LatLng(latitudeValue.value, longitudeValue.value),
            animation: google.maps.Animation.BOUNCE,
        });
        map.panTo(marker.getPosition());
    }
}

function intializePlayer() {
    videoPlayer = document.getElementById("videoPlayer");
    btnRestart = document.getElementById("btnRestart");
    btnPlay = document.getElementById("btnPlay");
    btnStop = document.getElementById("btnStop");
    btnBackward = document.getElementById("btnBackward");
    btnForward = document.getElementById("btnForward");
    btnFullscreen = document.getElementById("btnFullscreen");
    seekSliderTime = document.getElementById("seekSliderTime");
    currentTime = document.getElementById("currentTime");
    durationTime = document.getElementById("durationTime");

    videoPlayer.addEventListener("timeupdate", seekTimeUpdate, false);
    btnRestart.addEventListener("click", restart, false);
    btnPlay.addEventListener("click", playPause, false);
    btnStop.addEventListener("click", stop, false);
    btnBackward.addEventListener("click", backward, false);
    btnForward.addEventListener("click", forward, false);
    btnFullscreen.addEventListener("click", fullscreen, false);
    seekSliderTime.addEventListener("change", videoSeek, false);
    seekTimeUpdate();
}

function restart() {
    videoPlayer.currentTime = 0;
}

function playPause() {
    if (videoPlayer.paused) {
        videoPlayer.play();
        btnPlay.innerHTML = "<img class=\"system_icons imgPause\" />";
    } else {
        videoPlayer.pause();
        btnPlay.innerHTML = "<img class=\"system_icons imgPlay\" />";
    }
}

function stop() {
    videoPlayer.pause();
    videoPlayer.currentTime = 0;
    btnPlay.innerHTML = "<img class=\"system_icons imgPlay\" />";
}

function skip(value) {
    videoPlayer.currentTime += value;
}

function backward() {
    skip(-10);
}

function forward() {
    skip(10);
}

function fullscreen() {
    if (videoPlayer.requestFullScreen) {
        videoPlayer.requestFullScreen();
    } else if (videoPlayer.webkitRequestFullScreen) {
        videoPlayer.webkitRequestFullScreen();
    } else if (videoPlayer.mozRequestFullScreen) {
        videoPlayer.mozRequestFullScreen();
    }
}

function videoSeek() {
    var seekTo = videoPlayer.duration * (seekSliderTime.value / 100);
    videoPlayer.currentTime = seekTo;
}

function seekTimeUpdate() {
    var nt = videoPlayer.currentTime * (100 / videoPlayer.duration);
    seekSliderTime.value = nt;
    var currentMins = Math.floor(videoPlayer.currentTime / 60);
    var currentSecs = Math.floor(videoPlayer.currentTime - currentMins * 60);
    var durationMins = Math.floor(videoPlayer.duration / 60);
    var durationSecs = Math.floor(videoPlayer.duration - durationMins * 60);
    if (currentSecs < 10) {
        currentSecs = "0" + currentSecs;
    }
    if (durationSecs < 10) {
        durationSecs = "0" + durationSecs;
    }
    if (currentMins < 10) {
        currentMins = "0" + currentMins;
    }
    if (durationMins < 10) {
        durationMins = "0" + durationMins;
    }
    currentTime.innerHTML = currentMins + ":" + currentSecs;
    durationTime.innerHTML = durationMins + ":" + durationSecs;
}