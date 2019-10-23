$(document).ready(function(){
    jQuery.support.cors = true;
    $('#login').click(function (){
        var user = $('#username').val();
        var pssw = $('#psswd').val();
        var token = '';
        if(user == '' && pssw == ''){
            alert("Preencha todos os dados");
            
        }
        else{
            $.ajax({
                url: "http://192.168.223.65:8080/login",
                type: 'POST',
                dataType : 'json',
                contentType: "application/json; charset=utf-8",
                crossDomain:true,
                data:JSON.stringify({"email":user,"password":pssw}),
                success:function(data){
                    console.log(data);

                    if(data.data.auth == true && data.data.id_user_type=="1"){
                        Cookies.set('token',data.data.token);
                        console.log("Cookie: "+Cookies.get('token'));
                        alert(data.data);
                        window.location.href = "index.html";
                    }
                    else{
                        alert("Enganou-se no username e/ou password, tente novamente");
                    }
                },
                error: function (xhr, ajaxOptions, thrownError) {
                    console.log(JSON.stringify(xhr));
                    console.log(JSON.stringify(thrownError));
                    alert("Enganou-se no username e/ou password, tente novamente");
                    alert(xhr.status);
                    alert(thrownError);
                }
            });
        }
    });
    $('#register').click(function (){
        window.location.href = "useradd.html";
    });
});