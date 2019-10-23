(function ($){
    function changePr(e){
        var index = $('#id').val();
        $.ajax({
            url: "http://192.168.223.65:8080/reference_points"+'/'+index,
            type:'PUT',
            dataType:'json',
            contentType: "application/json; charset=utf-8",
            data:JSON.stringify({"id":$('#id').val(),"name":$('#name').val(), "coordx":$('#coordx').val(), "coordy":$('#coordy').val(),"isOffline":$('select option:selected').val(), "id_space":$('#id_space').val()}),
            processData: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
             },
            success:function(response){
                console.log(JSON.stringify(response));
                alert(response);
                window.location.href = "prvisual.html";
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
              }
        });
        e.preventDefault();
    }
    $('#alt').click( changePr );
}(jQuery));