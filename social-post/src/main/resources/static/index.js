var stompClient = null;

const searchParams = new URLSearchParams(window.location.search);
console.log("UUID = " + searchParams.get('uuid'));
console.log("JWT = " + searchParams.get('token'));

function connect() {
    const box = document.getElementById("box");
    box.innerHTML += "<br />";
    box.innerHTML += "UUID = " + searchParams.get('uuid') +  "<br />";
    box.innerHTML += "JWT = " + searchParams.get('token') +  "<br />";
    console.log("connect");
    var socket = new SockJS('http://localhost:8080/ws');
    stompClient = Stomp.over(socket);
    //stompClient.connect({"Authorization":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lcmF0ZWQxQHRlc3QucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiI4ODRkZmM3NS1iZjk0LTRhNDQtYmY0Yi05NzdiYTFiYzdkNGIiLCJpYXQiOjE2OTQwMjkwNzYsImV4cCI6MTY5NDEyOTU3Nn0.79JiVAVNuoQGGcXxP0INtJSkJyvWEuxNmRs4GjNMrKg"}, function (frame) {
    stompClient.connect({"Authorization": searchParams.get("token")}, function (frame) {
        console.log('Connected : ' + frame);
        box.innerHTML += "Connected : " + frame +  "<br />";
        //stompClient.subscribe("/user/884dfc75-bf94-4a44-bf4b-977ba1bc7d4b/queue/messages", function (message) {
        stompClient.subscribe("/user/" + searchParams.get("uuid") + "/queue/messages", function (message) {
            console.log(JSON.stringify(message));
            box.innerHTML += "" + JSON.stringify(message) +  "<br />"
        //}, {"Authorization":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZW5lcmF0ZWQxQHRlc3QucnUiLCJyb2xlIjoiVVNFUiIsInV1aWQiOiI4ODRkZmM3NS1iZjk0LTRhNDQtYmY0Yi05NzdiYTFiYzdkNGIiLCJpYXQiOjE2OTQwMjkwNzYsImV4cCI6MTY5NDEyOTU3Nn0.79JiVAVNuoQGGcXxP0INtJSkJyvWEuxNmRs4GjNMrKg"});
        }, {"Authorization": searchParams.get("token")});
    });
}

connect();


