$(document).ready(function(){
    jQuery.support.cors = true;
    $.ajax({
        url: "http://192.168.223.65:8080/users",
        type:'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data:{},
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
         },
        success: function(data){
            console.log(data);
            getUsers(data);
      },
        error: function (xhr, ajaxOptions, thrownError) {
          alert(xhr.status);
          alert(thrownError);
        }
    });      
});

function getUsers(data){
  for(i=0;i<data.data.length;i++){
        $("#users tbody").append("<tr>"+"<td>"+data.data[i].id_user+"</td>"
        + "<td>"+data.data[i].name+"</td>"
        +"<td>"+data.data[i].email+"</td>"
        +"<td>"+data.data[i].username+"</td>"
        +"<td>"+data.data[i].birthday+"</td>"
        +"<td>"+data.data[i].date_registration+"</td>"
        +"<td>"+data.data[i].id_user_type+"</td>"
        +"</tr>")
    }
}


(function ($){
    function deletePr(e){
        var index = $('#id').val();
        $.ajax({
            url: "http://192.168.223.65:8080/users"+'/'+index,
            type:'DELETE',
            dataType:'json',
            contentType: "application/json; charset=utf-8",
            data:JSON.stringify({"id":$('#id').val()}),
            processData: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Bearer "+ Cookies.get('token'));
            },
            success:function(response){
                console.log(JSON.stringify(response));
                alert(response);
                window.location.href = "userdata.html";
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
              }
        });
        e.preventDefault();
    }
    $('#eli').click( deletePr );
}(jQuery));