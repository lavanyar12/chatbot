var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#send").prop("disabled", !connected);
    $("#disconnect").prop("disabled", !connected);
    $("#chat").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#botResponses").html("");
}

function connect() {
    var socket = new SockJS('/chat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/chatbot', function (response) {
            var workflowId = JSON.parse(response.body).workflowId;
            showChatText();
            var message = JSON.parse(response.body).content;
            showChatbotResponse(workflowId ? message : message + '<br><br>You may exit the chat now');
            document.getElementById("workflowId").value = workflowId;
            if (!workflowId) {
                document.getElementById("name").value = null;
                toggleInputs(true)
                $("#chat").prop("disabled", true);
                $("#send").prop("disabled", true);
            }
        });
        sendChatText();
    });
}

function toggleInputs(connected) {
    $("#connect").prop("disabled", connected);
    $("#chat").prop("disabled", connected);
    $("#send").prop("disabled", connected)
    $("#disconnect").prop("disabled", !connected);
    document.getElementById("chat").reset();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendChatText() {
    stompClient.send("/app/chat", {}, JSON.stringify({'chatText': $("#chat").val(), 'workflowId': $("#workflowId").val()}));
}

function showChatText(message) {
    if ($("#chat").val().length) {
        $("#botResponses").append("<tr><td>" + 'You => ' + $("#chat").val() + "</td></tr>");
    }
}

function showChatbotResponse(message) {
    $("#botResponses").append("<tr><td>" + 'BOT => ' + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendChatText(); });
});
