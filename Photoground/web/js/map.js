var imgLoading = document.getElementById("imgLoading");
var geocoder;
var map;
var infowindow;
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
}

var point = [];
var content = [];
var markers = [];
var index = 0;

function drop() {
    index = 0;
    markers = [];
    for (var i = 0; i < point.length; i++) {
        setTimeout(function() {
            addMarker();
        }, i * 200);
        //addMarker();
    }
}

function addMarker() {
    var image = 'images/camera_icon.png';
    markers.push(new google.maps.Marker({
        position: point[index],
        map: map,
        draggable: false,
        icon: image, 
        customInfo: index, 
        animation: google.maps.Animation.DROP
    }));

    var markerInfo = markers[markers.length - 1];

    infowindow = new google.maps.InfoWindow({});

    google.maps.event.addListener(markerInfo, 'click', function(event) {
        infowindow.setContent(content[this.customInfo]);
        infowindow.open(map, markerInfo);
        map.panTo(markerInfo.getPosition());
    });
    index++;
}

function clearMarkers() { 
    for (var i = 0; i < markers.length; i++) {
         markers[i].setMap(null);
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
        //var image = 'images/scanners_cameras.png';
        geocoder.geocode({'address': address.value}, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                map.setCenter(results[0].geometry.location);
                //alert(results[0].geometry.location + "\n" + results[0].geometry.viewport);
                loadAddress(results[0].geometry.viewport);
                /*
                var marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location,
                    icon: image,
                    animation: google.maps.Animation.DROP
                });
                */
            } else {
                alert("ไม่พบข้อมูล");
                imgLoading.style.display = "none";
                //alert("Geocode was not successful for the following reason: " + status);
            }
        });
    }
}

function loadAddress(viewport) {
    clearMarkers();
    var data = "";
    data += "viewport=" + viewport;
    ajaxLoad("get", "Address.json", data, "", "markPoint");
}

function markPoint(jsonData) {
    point = [];
    content = [];
    var rootPath = document.getElementById("rootPath");
    var items = jsonData.Addresses.length;
    for (item = 0; item < items; item++) {
        point.push(new google.maps.LatLng(jsonData.Addresses[item].LatitudeMap, jsonData.Addresses[item].LongitudeMap));
        content.push("<a href='" + rootPath.value + "Photo/" + jsonData.Addresses[item].RowID + "' target='_blank'><img alt='' src='" + jsonData.Addresses[item].FileThumbnailPath + "' border='0' /></a>");
    }
    drop();
    imgLoading.style.display = "none";
}

google.maps.event.addDomListener(window, 'load', initialize);