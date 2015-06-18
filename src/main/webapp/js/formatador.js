function maskCPF(CPF) {
	var evt = window.event;
	kcode = evt.keyCode;
	if (kcode == 8)
		return;

	switch (CPF.value.length) {
	case 3:
		CPF.value = CPF.value + '.';
		break;
	case 7:
		CPF.value = CPF.value + '.';
		break;
	case 11:
		CPF.value = CPF.value + '-';
		break;
	}
}

function maskData(obj, prox) {
	switch (obj.value.length) {
	case 2:
		obj.value = obj.value + "/";
		break;
	case 5:
		obj.value = obj.value + "/";
		break;
	case 10:
		prox.focus();
		break;
	}
}

function formataCPF(value) {
	with (value) {
		value = value.substr(0, 3) + '.' + value.substr(3, 3) + '.'
				+ value.substr(6, 3) + '-' + value.substr(9, 2);
	}
}

function fone(obj, prox) {
	switch (obj.value.length) {
	case 1:
		obj.value = "(" + obj.value;
		break;
	case 3:
		obj.value = obj.value + ")";
		break;
	case 8:
		obj.value = obj.value + "-";
		break;
	case 13:
		prox.focus();
		break;
	}
}

function formata_cep(obj, prox) {
	switch (obj.value.length) {
	case 2:
		obj.value = obj.value + ".";
		break;
	case 6:
		obj.value = obj.value + "-";
		break;
	case 10:
		prox.focus();
		break;
	}
}

// ******************************************************************************************
function replace(valor) {

	valor = valor.toString().replace("a", "");
	valor = valor.toString().replace("b", "");
	valor = valor.toString().replace("c", "");
	valor = valor.toString().replace("d", "");
	valor = valor.toString().replace("e", "");
	valor = valor.toString().replace("f", "");
	valor = valor.toString().replace("g", "");
	valor = valor.toString().replace("h", "");
	valor = valor.toString().replace("i", "");
	valor = valor.toString().replace("j", "");
	valor = valor.toString().replace("k", "");
	valor = valor.toString().replace("l", "");
	valor = valor.toString().replace("m", "");
	valor = valor.toString().replace("n", "");
	valor = valor.toString().replace("o", "");
	valor = valor.toString().replace("p", "");
	valor = valor.toString().replace("q", "");
	valor = valor.toString().replace("r", "");
	valor = valor.toString().replace("s", "");
	valor = valor.toString().replace("t", "");
	valor = valor.toString().replace("u", "");
	valor = valor.toString().replace("v", "");
	valor = valor.toString().replace("w", "");
	valor = valor.toString().replace("x", "");
	valor = valor.toString().replace("y", "");
	valor = valor.toString().replace("z", "");

	return valor;

}

function FormataValor(id, tammax, teclapres) { 
	if (window.event) { // Internet Explorer
		var tecla = teclapres.keyCode;
	} else if (teclapres.which) { // Nestcape / firefox
		var tecla = teclapres.which;
	}

	vr = document.getElementById( id ).value;
	vr = replace(vr);
	vr = vr.toString().replace("/", "");
	vr = vr.toString().replace("/", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(",", "");
	vr = vr.toString().replace(".", "");
	vr = vr.toString().replace(".", "");
	vr = vr.toString().replace(".", "");
	vr = vr.toString().replace(".", "");

	if ((tecla > 47 && tecla < 58)) { // numeros de 0 a 9

	} else {
		if (tecla != 8) { // backspace
			alert("Digite somente numeros!");
		} else {

		}
	}

	tam = vr.length;

	if (tam < tammax && tecla != 8) {
		tam = vr.length + 1;
	}

	if (tecla == 8) {
		tam = tam - 1;
	}

	if (tecla == 8 || tecla >= 48 && tecla <= 57 || tecla >= 96 && tecla <= 105) {
		if (tam <= 2) {
			document.getElementById(id).value = vr;
		}
		if ((tam > 2) && (tam <= 5)) {
			document.getElementById(id).value = vr.substr(0, tam - 2) + ','
					+ vr.substr(tam - 2, tam);
		}
		if ((tam >= 6) && (tam <= 8)) {
			document.getElementById(id).value = vr.substr(0, tam - 5) + '.'
					+ vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam);
		}
		if ((tam >= 9) && (tam <= 11)) {
			document.getElementById(id).value = vr.substr(0, tam - 8) + '.'
					+ vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3) + ','
					+ vr.substr(tam - 2, tam);
		}
		if ((tam >= 12) && (tam <= 14)) {
			document.getElementById(id).value = vr.substr(0, tam - 11) + '.'
					+ vr.substr(tam - 11, 3) + '.' + vr.substr(tam - 8, 3)
					+ '.' + vr.substr(tam - 5, 3) + ','
					+ vr.substr(tam - 2, tam);
		}
		if ((tam >= 15) && (tam <= 17)) {
			document.getElementById(id).value = vr.substr(0, tam - 14) + '.'
					+ vr.substr(tam - 14, 3) + '.' + vr.substr(tam - 11, 3)
					+ '.' + vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3)
					+ ',' + vr.substr(tam - 2, tam);
		}
	}
}

//Aplica a m�scara no campo
//Função para ser utilizada nos eventos do input para formata��o din�mica
function aplica_mascara_cpfcnpj(campo,tammax,teclapres) {
	var tecla = teclapres.keyCode;

	if ((tecla < 48 || tecla > 57) && (tecla < 96 || tecla > 105) && tecla != 46 && tecla != 8) {
		return false;
	}

	var vr = campo.value; 
	vr = vr.replace( /\//g, "" );
	vr = vr.replace( /-/g, "" );
	vr = vr.replace( /\./g, "" );
	var tam = vr.length;

	if ( tam <= 2 ) {
		campo.value = vr;
	}
	if ( (tam > 2) && (tam <= 5) ) {
		campo.value = vr.substr( 0, tam - 2 ) + '-' + vr.substr( tam - 2, tam );
	}
	if ( (tam >= 6) && (tam <= 8) ) {
		campo.value = vr.substr( 0, tam - 5 ) + '.' + vr.substr( tam - 5, 3 ) + '-' + vr.substr( tam - 2, tam );
	}
	if ( (tam >= 9) && (tam <= 11) ) {
		campo.value = vr.substr( 0, tam - 8 ) + '.' + vr.substr( tam - 8, 3 ) + '.' + vr.substr( tam - 5, 3 ) + '-' + vr.substr( tam - 2, tam );
	}
	if ( (tam == 12) ) {
		campo.value = vr.substr( tam - 12, 3 ) + '.' + vr.substr( tam - 9, 3 ) + '/' + vr.substr( tam - 6, 4 ) + '-' + vr.substr( tam - 2, tam );
	}
	if ( (tam > 12) && (tam <= 14) ) {
		campo.value = vr.substr( 0, tam - 12 ) + '.' + vr.substr( tam - 12, 3 ) + '.' + vr.substr( tam - 9, 3 ) + '/' + vr.substr( tam - 6, 4 ) + '-' + vr.substr( tam - 2, tam );
	}
}

//Verifica se CPF ou CGC e encaminha para a devida fun��o, no caso do cpf/cgc estar digitado sem mascara
function verifica_cpf_cnpj(cpf_cnpj) {
	if (cpf_cnpj.length == 11) {
		return(verifica_cpf(cpf_cnpj));
	} else if (cpf_cnpj.length == 14) {
		return(verifica_cnpj(cpf_cnpj));
	} else { 
		return false;
	}
	return true;
}

//Verifica se o n�mero de CPF informado � v�lido
function verifica_cpf(sequencia) {
	if ( Procura_Str(1,sequencia,'00000000000,11111111111,22222222222,33333333333,44444444444,55555555555,66666666666,77777777777,88888888888,99999999999,00000000191,19100000000') > 0 ) {
		return false;
	}
	seq = sequencia;
	soma = 0;
	multiplicador = 2;
	for (f = seq.length - 3;f >= 0;f--) {
		soma += seq.substring(f,f + 1) * multiplicador;
		multiplicador++;
	}
	resto = soma % 11;
	if (resto == 1 || resto == 0) {
		digito = 0;
	} else {
		digito = 11 - resto;
	}
	if (digito != seq.substring(seq.length - 2,seq.length - 1)) {
		return false;
	}
	soma = 0;
	multiplicador = 2;
	for (f = seq.length - 2;f >= 0;f--) {
		soma += seq.substring(f,f + 1) * multiplicador;
		multiplicador++;
	}
	resto = soma % 11;
	if (resto == 1 || resto == 0) {
		digito = 0;
	} else {
		digito = 11 - resto;
	}
	if (digito != seq.substring(seq.length - 1,seq.length)) {
		return false;
	}
	return true;
}

//Verifica se o n�mero de CNPJ informado � v�lido
function verifica_cnpj(sequencia) {
	seq = sequencia;
	soma = 0;
	multiplicador = 2;
	for (f = seq.length - 3;f >= 0;f-- ) {
		soma += seq.substring(f,f + 1) * multiplicador;
		if ( multiplicador < 9 ) {
			multiplicador++;
		} else {
			multiplicador = 2;
		}
	}
	resto = soma % 11;
	if (resto == 1 || resto == 0) {
		digito = 0;
	} else {
		digito = 11 - resto;
	}
	if (digito != seq.substring(seq.length - 2,seq.length - 1)) {
		return false;
	}

	soma = 0;
	multiplicador = 2;
	for (f = seq.length - 2;f >= 0;f--) {
		soma += seq.substring(f,f + 1) * multiplicador;
		if (multiplicador < 9) {
			multiplicador++;
		} else {
			multiplicador = 2;
		}
	}
	resto = soma % 11;
	if (resto == 1 || resto == 0) {
		digito = 0;
	} else {
		digito = 11 - resto;
	}
	if (digito != seq.substring(seq.length - 1,seq.length)) {
		return false;
	}
	return true;
}

//Procura uma string dentro de outra string
function Procura_Str(param0,param1,param2) {
	for (a = param0 - 1;a < param1.length;a++) {
		for (b = 1;b < param1.length;b++) {
			if (param2 == param1.substring(b - 1,b + param2.length - 1)) {
				return a;
			}
		}
	}
	return 0;
}

//Retira a m�scara do valor de cpf_cnpj
function retira_mascara(cpf_cnpj) {
	return cpf_cnpj.replace(/\./g,'').replace(/-/g,'').replace(/\//g,'')
}

//adiciona mascara de cep
function MascaraCep(cep){
		if(mascaraInteiro(cep)==false){
		event.returnValue = false;
	}	
	return formataCampo(cep, '00.000-000', event);
}

//adiciona mascara de data
function MascaraData(data){
	if(mascaraInteiro(data)==false){
		event.returnValue = false;
	}	
	return formataCampo(data, '00/00/0000', event);
}

//valida CEP
function ValidaCep(cep){
	exp = /\d{2}\.\d{3}\-\d{3}/
	if(!exp.test(cep.value))
		alert('Numero de Cep Invalido!');		
}

//valida data
function ValidaData(data){
	exp = /\d{2}\/\d{2}\/\d{4}/
	if(!exp.test(data.value)){
		data.value = "";
		alert('Data Invalida!');
		data.select();		
}
}

//valida numero inteiro com mascara
function mascaraInteiro(){
	if (event.keyCode < 48 || event.keyCode > 57){
		event.returnValue = false;
		return false;
	}
	return true;
}

//formata de forma generica os campos
function formataCampo(campo, Mascara, evento) { 
	var boleanoMascara; 
	
	var Digitato = evento.keyCode;
	exp = /\-|\.|\/|\(|\)| /g
	campoSoNumeros = campo.value.toString().replace( exp, "" ); 
   
	var posicaoCampo = 0;	 
	var NovoValorCampo="";
	var TamanhoMascara = campoSoNumeros.length;; 
	
	if (Digitato != 8) { // backspace 
		for(i=0; i<= TamanhoMascara; i++) { 
			boleanoMascara  = ((Mascara.charAt(i) == "-") || (Mascara.charAt(i) == ".")
								|| (Mascara.charAt(i) == "/")) 
			boleanoMascara  = boleanoMascara || ((Mascara.charAt(i) == "(") 
								|| (Mascara.charAt(i) == ")") || (Mascara.charAt(i) == " ")) 
			if (boleanoMascara) { 
				NovoValorCampo += Mascara.charAt(i); 
				  TamanhoMascara++;
			}else { 
				NovoValorCampo += campoSoNumeros.charAt(posicaoCampo); 
				posicaoCampo++; 
			  }	   	 
		  }	 
		campo.value = NovoValorCampo;
		  return true; 
	}else { 
		return true; 
	}
}