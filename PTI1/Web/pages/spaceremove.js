$(document).ready(function(){
    jQuery.support.cors = true;
    $.ajax({
        url: "http://192.168.223.65:8080/spaces",
        contentType: "application/json; charset=utf-8",
        type:'GET',
        dataType: "json",
        data:{},
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
        }, 
        success: function(data){
            console.log(data);
            getSpaces(data);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            alert(xhr.status);
            alert(thrownError);
      }
    });      
});

function getSpaces(data){
    for(var i=0;i<data.data.length;i++){
          $("#spaces tbody").append("<tr>"+"<td>"+data.data[i].id_space+"</td>"
          +"<td>"+"<div class = dropdown>"+"<img src = https://dummyimage.com/100x50/000/fff.png&text=Imagem>"+"<div class = dropdown-content>"+"<img src = https://dummyimage.com/"+data.data[i].map_width+"x"+data.data[i].map_length+"/000/fff.png&text="+data.data[i].name+">"+"</div>"+"</div>"+"</td>"
            + "<td>"+data.data[i].name+"</td>"
            +"<td>"+data.data[i].description+"</td>"
            +"<td>"+data.data[i].map_path+"</td>"
            +"<td>"+data.data[i].map_width+"</td>"
            +"<td>"+data.data[i].map_length+"</td>"
            +"<td>"+data.data[i].id_user+"</td>"
            +"</tr>")
           
    }
  
}

(function ($){
    function deleteSpace(e){
        var index = $('#id').val();
        $.ajax({
            url: "http://192.168.223.65:8080/spaces"+'/'+index,
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
                window.location.href = "espacevisual.html";
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
              }
        });
        e.preventDefault();
    }
    $('#eli').click( deleteSpace );
}(jQuery));