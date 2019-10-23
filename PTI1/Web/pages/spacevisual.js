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
        var nome = data.data[i].name.replace(/ /g, "+");
          $("#spaces tbody").append("<tr>"+"<td>"+data.data[i].id_space+"</td>"
          +"<td>"+"<div class = dropdown>"+"<img src = https://dummyimage.com/100x50/000/fff.png&text=Imagem>"+"<div class = dropdown-content>"+"<img src = https://dummyimage.com/"+data.data[i].map_width+"x"+data.data[i].map_length+"/000/fff.png&text="+nome+">"+"</div>"+"</div>"+"</td>"
            + "<td>"+data.data[i].name+"</td>"
            +"<td>"+data.data[i].description+"</td>"
            +"<td>"+data.data[i].map_path+"</td>"
            +"<td>"+data.data[i].map_width+"</td>"
            +"<td>"+data.data[i].map_length+"</td>"
            +"<td>"+data.data[i].id_user+"</td>"
            +"</tr>")
           
    }
  
}