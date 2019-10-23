(function ($){
    function addPontInt(e){
        $.ajax({
            url: "http://192.168.223.65:8080/interest_points",
            type:'POST',
            dataType:'json',
            contentType: "application/json; charset=utf-8",
            data:JSON.stringify({"name":$('#name').val(), "coordx":$('#coordx').val(), "coordy":$('#coordy').val(),"id_space":$('#id_space').val()}),
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
    $('#add').click( addPontInt );
}(jQuery));