$(document).ready(function(){
    jQuery.support.cors = true;
    $.ajax({
        url: "http://192.168.223.65:8080/contributions_history",
        type:'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data:{},
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
         },
        success: function(data){
            console.log(data);
            getContriVisual(data);
        },
        error: function (xhr, ajaxOptions, thrownError) {
        alert(xhr.status);
        alert(thrownError);
      }
    });      
});

function getContriVisual(data){
        for(i=0;i<data.data.length;i++){
            $("#conts tbody").append("<tr>"+"<td>"+data.data[i].id_contribution_history+"</td>"
            + "<td>"+data.data[i].date+"</td>"
            +"<td>"+data.data[i].coordx_offset+"</td>"
            +"<td>"+data.data[i].coordy_offset+"</td>"
            +"<td>"+data.data[i].id_reference_point+"</td>"
            +"<td>"+data.data[i].id_user+"</td>"
            +"</tr>")
        }
}

function getContriVisualbyID(){
    console.log("here");
    $("#inp").on("keyup",function(){
      var index = $(this).val().toLowerCase();
      $("#conts tr").filter(function(){
        console.log(this);
        $(this).toggle($(this).text().indexOf(index) > -1);
      });
    });
  }; 
  
  $('#pes').click(getContriVisualbyID);