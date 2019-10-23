$(document).ready(function(){
    jQuery.support.cors = true;
    $.ajax({
        url: "http://192.168.223.65:8080/interest_points",
        type:'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        beforeSend: function (xhr) {
          xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
       },
        data:{}, 
        success: function(data){
            console.log(data);
            getPontInt(data);
            
      },
        error: function (xhr, ajaxOptions, thrownError) {
          alert(xhr.status);
          alert(thrownError);
        }
    });      
});

function getPontInt(data){
  for(i=0;i<data.data.length;i++){
    $("#intpoints tbody").append("<tr>"+"<td>"+data.data[i].id_interest_point+"</td>"
    + "<td>"+data.data[i].name+"</td>"
    +"<td>"+data.data[i].coordx+"</td>"
    +"<td>"+data.data[i].coordy+"</td>"
    +"<td>"+data.data[i].id_space+"</td>"
    +"</tr>")
  }
}

(function ($){
    function deletePI(e){
        var index = $('#id').val();
        $.ajax({
            url: "http://192.168.223.65:8080/interest_points"+'/'+index,
            type:'DELETE',
            dataType:'json',
            contentType: "application/json; charset=utf-8",
            data:JSON.stringify({"id":$('#id').val()}),
            processData: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
            },
            success:function(response){
                console.log(JSON.stringify(response));
                alert(response);
                window.location.href = "pivisual.html";
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
              }
        });
        e.preventDefault();
    }
    $('#eli').click( deletePI );
}(jQuery));