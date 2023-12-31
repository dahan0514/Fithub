const openMessageButtons = document.querySelectorAll(".messageOpen");
const closeMessage = document.querySelector(".messageClose");
const messageModalBox = document.querySelector('.message_modal_box');

closeMessage.addEventListener("click", () => {
    messageModalBox.classList.remove("active");
    messageContent.value = '';
    messageAdminFormBox.style.display = "none";
});

// 모달 외부 클릭 시 모달 창 닫기
messageModalBox.addEventListener('click', (e) => {
    if (e.target === messageModalBox) {
        messageModalBox.classList.remove("active");
        messageContent.value = '';
        messageAdminFormBox.style.display = "none";
    }
});

function resetModalContent() {
    messageContent.value = '';
}

$(".resetMessageButton").on("click", function (e) {
    e.preventDefault();
    messageContent.value = '';
});

function messageModal(e){
    e.preventDefault();

    var messageNum = $(this).closest('tr').find('.messageNum').text();
    var messageUserId = $(this).closest('tr').find('.messageUserId').text();

    document.querySelector('#messageNum').value = messageNum;
    document.querySelector('#messageUserId').value = messageUserId;
    messageModalBox.classList.add("active");

    sendMessageNumToModal(messageNum);
}

function sendMessageNumToModal(messageNum) {
    $.ajax({
        url: '/adminmypage/messageModal',
        method: 'POST',
        data: { messageNum: messageNum },
        success: function(data) {
            messageModalDom(data);
        },
        error: function(error) {
        }
    });
}

function messageModalDom(data) {
    var data = JSON.parse(data);
    var tableBody = $('#message-table');
    tableBody.empty();

    var row = $('<tr style="text-align: center;">');
    row.append('<td class="long_text">' + data.messageDTO.messageNum + '</td>');
    row.append('<td class="long_text"><a href="#" class="open">' + data.messageDTO.sendId + '</a></td>');
    row.append('<td class="long_text">' + data.messageDTO.sendDate + '</td>');
    row.append('<td class="long_text"><a href="#" class="messageFormOpen button btnColor">처리하기</a></td>');
    tableBody.append(row);

    var newRow = $('<tr style="text-align: center;">');
    newRow.append('<td class="long_text" style="font-weight: bold;">문의 내용</td>');
    newRow.append('<td class="word_wrap_test" colspan="3">' + data.messageDTO.messageContent + '</td>');
    tableBody.append(newRow);


    $('.open').on('click', profileModalOpen);
    $('.messageFormOpen').on('click', messageFormOpen);
}

document.querySelector('.confirmMessageButton').addEventListener('click', function() {
    const messageContent = document.querySelector('#messageContent').value;
    const receiveId = document.querySelector('#messageUserId').value;
    const messageNum = document.querySelector('#messageNum').value;

    const data = {
        messageContent: messageContent,
        receiveId: receiveId,
        messageNum: messageNum
    };

    fetch('/adminmypage/messageReturn', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            window.alert('문의 답변 완료!!');
            location.reload();
        } else {
            window.alert('문의 답변 실패. 다시 시도하세요.');
        }
    })
    .catch(error => {
        window.alert('오류 발생: ' + error.message);
    });

    messageContent.value = '';
    messageAdminFormBox.style.display = "none";
    messageModalBox.classList.remove("active");
});

document.querySelector('.cancelMessageButton').addEventListener('click', function() {
    messageModalBox.classList.remove("active");
    messageContent.value = '';
    messageAdminFormBox.style.display = "none";
});

function messageFormOpen(e){
    e.preventDefault();

    const messageAdminFormBox = document.getElementById("messageAdminFormBox");
    const messageFormOpen = document.querySelector('.messageFormOpen');

    messageAdminFormBox.style.display = "block";
    messageFormOpen.classList.add('primary');
    messageFormOpen.classList.add('disabled');
}
