(function ($){
    var bdy = $("#bd").on('change',function(){
            var $this = $(this);
            console.log("String: ",$this.val());
            console.log("Data: ",$this.prop("valueAsDate"));
    });

    var rgt = $("#rg").on('change',function(){
        var $this = $(this);
        console.log("String: ",$this.val());
        console.log("Data: ",$this.prop("valueAsDate"));
    });


    function setUser(e){
        $.ajax({
            url: "http://192.168.223.65:8080/users/2",
            type:'PUT',
            dataType:'json',
            contentType: "application/json; charset=utf-8",
            data:JSON.stringify({"name":$('#name').val(), "username":$('#username').val(), "password":$('#pssw').val(), "email":$('#email').val(),"address":$('#address').val(),"birthday":bdy.val(),"date_registration":rgt.val(),"id_user_type":$('select option:selected').val()}),
            processData: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", "Bearer "+Cookies.get('token'));
            },
            success:function(response){
                console.log(JSON.stringify(response));
                alert(response);
                window.location.href = "index.html";
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
              }
            });
        e.preventDefault();
    }
    $('#put').click( setUser );
    $('#canc').click(function(){
        window.location.href = "index.html";   
    });
}(jQuery));