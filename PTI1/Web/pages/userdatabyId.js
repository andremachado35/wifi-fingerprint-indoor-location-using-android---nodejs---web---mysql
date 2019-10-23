$(document).ready(function(){
    jQuery.support.cors = true;
    function getUserbyID(e){
        var index = $('#input').val();
        $.ajax({
            url: "http://192.168.223.65:8080/users"+'/'+index,
            type:'GET',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data:{},
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
             },
            success: function(response){
              $('#us_id').append(response.data[0].id_user);
              $('#name').append(response.data[0].name);
              $('#user').append(response.data[0].username);
              $('#mail').append(response.data[0].email);
              $('#addr').append(response.data[0].address);
              $('#birth').append(response.data[0].birthday);
              $('#reg').append(response.data[0].date_registration);
              $('#type').append(response.data[0].id_user_type);
            
              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#us_id').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#name').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#user').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#mail').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#addr').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#birth').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#reg').empty();
              });

              $('#sb').on('click',function(e){
                e.preventDefault();
                $('#type').empty();
              });
            },
            error: function (xhr, ajaxOptions, thrownError) {
              alert(xhr.status);
              alert(thrownError);
            }
        });
        e.preventDefault();
    }
    $('#sb').click(getUserbyID); 

}(jQuery));

    