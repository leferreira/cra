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
    $("#cnpj").mask("99.999.999/9999-99");
	$("#cep").mask("99.999-999");
	$("#telefone").mask("(99) 9999-9999");
	$("#celular").mask("(99) 9999-9999");
});