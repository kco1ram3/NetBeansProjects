var imgLoading = document.getElementById("imgLoading");
var latitude = document.getElementById("latitude");
var longitude = document.getElementById("longitude");
var latitudeValue = document.getElementById("latitudeValue");
var longitudeValue = document.getElementById("longitudeValue");
var coordinates = document.getElementById("coordinates");
var search_map = document.getElementById("search_map");
var gmap = document.getElementById("gmap");
var isEditable = true;
var geocoder;
var map;
var marker;

function checkUpload() { 
    var isValid = true;
    var fileType = "";
    var photoType = ".jpg,.jpeg,.png,.gif";
    var videoType = ".mp4";
    var filePath = document.getElementById("filePath");
    var photo = document.getElementById("photo");
    var video = document.getElementById("video");
    if (filePath.value === "" && photo.value === "") {
        photo.className = "invalid_textbox";
        isValid = false;
    } else {
        photo.className = photo.className.replace("invalid_textbox", "");
    }

    if (photo.value !== "") {
        fileType = photo.value;
        fileType = fileType.toLowerCase().substring(fileType.lastIndexOf("."), fileType.length);
        if (photoType.indexOf(fileType) == -1) {
            photo.className = "invalid_textbox";
            isValid = false;
        } else {
            photo.className = photo.className.replace("invalid_textbox", "");
        }
    }

    if (video.value !== "") {
        fileType = video.value;
        fileType = fileType.toLowerCase().substring(fileType.lastIndexOf("."), fileType.length);
        if (videoType.indexOf(fileType) == -1) {
            video.className = "invalid_textbox";
            isValid = false;
        } else {
            video.className = video.className.replace("invalid_textbox", "");
        }
    } 
    return isValid;
}

function initialize() {
    imgLoading.style.display = "none";
    geocoder = new google.maps.Geocoder();
    var mapOptions = {
        zoom: 5,
        center: new google.maps.LatLng(13.7278956, 100.52412349999997),
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById('map-canvas'),
            mapOptions);

    google.maps.event.addListener(map, "rightclick", function(event) {
        if (isEditable) {
            var lat = event.latLng.lat();
            var lng = event.latLng.lng();
            addMarker(lat, lng);
        }
    });

    if ((latitudeValue.value != "" && longitudeValue.value != "") && (latitudeValue.value != "0" && longitudeValue.value != "0")) {
        addMarker(latitudeValue.value, longitudeValue.value);
    }
}

function codeAddress() {
    var isValid = true;
    var address = document.getElementById("address");
    if (address.value === "") {
        address.className = "invalid_textbox";
        isValid = false;
    } else {
        address.className = address.className.replace("invalid_textbox", "");
    }
    if (isValid) {
        imgLoading.style.display = "";
        geocoder.geocode({'address': address.value}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                //map.setCenter(results[0].geometry.location);
                var lat = results[0].geometry.location.lat();
                var lng = results[0].geometry.location.lng();
                //setTimeout(function(){ addMarker(lat, lng); }, 1000); 
                addMarker(lat, lng);
            } else {
                alert("ไม่พบข้อมูล");
                imgLoading.style.display = "none";
                //alert("Geocode was not successful for the following reason: " + status);
            }
        });
    }
}

function addMarker(lat, lng) {
    imgLoading.style.display = "none";
    if (typeof marker != "undefined") {
        marker.setMap(null);
    }
    try {
        latitude.value = lat.toFixed(5);
        longitude.value = lng.toFixed(5);
        latitudeValue.value = lat.toFixed(5);
        longitudeValue.value = lng.toFixed(5);
    } catch (e) {
        latitude.value = lat;
        longitude.value = lng;
        latitudeValue.value = lat;
        longitudeValue.value = lng;
    }
    marker = new google.maps.Marker({
        map: map,
        position: new google.maps.LatLng(lat, lng),
        animation: google.maps.Animation.BOUNCE, 
    });
    map.panTo(marker.getPosition());

    setDraggable();
}

function setDraggable() {
    marker.setOptions({draggable: isEditable});
    if (isEditable) {
        google.maps.event.addListener(marker, 'dragstart', function(event) {
            marker.setAnimation(null);
        });

        google.maps.event.addListener(marker, 'drag', function(event) {
            latitude.value = event.latLng.lat().toFixed(5);
            longitude.value = event.latLng.lng().toFixed(5);
            latitudeValue.value = event.latLng.lat().toFixed(5);
            longitudeValue.value = event.latLng.lng().toFixed(5);
        });

        google.maps.event.addListener(marker, 'dragend', function(event) {
            marker.setAnimation(google.maps.Animation.BOUNCE);
            map.panTo(marker.getPosition());
            latitude.value = event.latLng.lat().toFixed(5);
            longitude.value = event.latLng.lng().toFixed(5);
            latitudeValue.value = event.latLng.lat().toFixed(5);
            longitudeValue.value = event.latLng.lng().toFixed(5);
        });
    }
}

function changePosition() {
    addMarker(latitude.value, longitude.value);
}

function coordinatesType(value) {
    if (value == "1") {
        latitude.disabled = true;
        longitude.disabled = true;
        coordinates.style.display = "";
        search_map.style.display = "none";
        gmap.style.display = "";
        isEditable = false;
        getLocation();
    } else if (value == "2") {
        latitude.disabled = false;
        longitude.disabled = false;
        coordinates.style.display = "";
        search_map.style.display = "";
        gmap.style.display = "";
        isEditable = true;
        setDraggable();
    } else {
        coordinates.style.display = "none";
        gmap.style.display = "none";
    }
}

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    }
}

function showPosition(position) {
    addMarker(position.coords.latitude, position.coords.longitude);
}

google.maps.event.addDomListener(window, 'load', initialize);