$(document).ready(function(){
    jQuery.support.cors = true;
    function getSpacebyID(e){
        var index = $('#ind').val();
        $.ajax({
            url: "http://192.168.223.65:8080/spaces"+'/'+index,
            type:'GET',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data:{},
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
             },
            success: function(response){
              var nome = response.data[0].name.replace(/ /g, "+");
              $('#us_id').append(response.data[0].id_space);
              $('#name').append(response.data[0].name);
              $('#descr').append(response.data[0].description);
              $('#id_user').append(response.data[0].id_user);
              $('#img2').prepend($("<img src= https://dummyimage.com/"+response.data[0].map_width+"x"+response.data[0].map_length+"/000/fff.png&text="+nome+">"));
             
            
              $('#search').on('click',function(e){
                e.preventDefault();
                $('#us_id').empty();
              });

              $('#search').on('click',function(e){
                e.preventDefault();
                $('#name').empty();
              });

              $('#search').on('click',function(e){
                e.preventDefault();
                $('#descr').empty();
              });

              $('#search').on('click',function(e){
                e.preventDefault();
                $('#img2').empty();
              });

            },
            error: function (xhr, ajaxOptions, thrownError) {
              alert(xhr.status);
              alert(thrownError);
            }
        });
        e.preventDefault();
    }
    $('#search').click(getSpacebyID); 

}(jQuery));

    