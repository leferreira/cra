//$(function() {
//        $.mask.definitions['~'] = "[+-]";
//        $("#date").mask("99/99/9999",{completed:function(){alert("completed!");}});
//		$("#cpf").mask("999.999.999-99");
//		$("#cep").mask("99.999-999");
//        $("#telefone").mask("(99) 9999-9999");
//        $("#celular").mask("(99) 9999-9999");
//
//        
//        $("input").blur(function() {
//            $("#info").html("Unmasked value: " + $(this).mask());
//        }).dblclick(function() {
//            $(this).unmask();
//        });
//    });

$().ready(function() {
    $("#cpf").mask("999.999.999-99");
    $("#data").mask("99/99/9999");
    $("#data1").mask("99/99/9999");
    $("#date").mask("99/99/9999");
    $("#date1").mask("99/99/9999");
    $("#cnpj").mask("99.999.999/9999-99");
	$("#cep").mask("99.999-999");
	$("#cep1").mask("99.999-999");
	$("#telefone").mask("(99)9999-9999");
	$("#celular").mask("(99)9999-9999");
	$("#contato").mask("(99)9999-9999");
	$("#hora").mask("99:99");
	$("#hora1").mask("99:99");
	$("#alinea").mask("99");
});