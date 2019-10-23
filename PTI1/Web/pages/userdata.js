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

