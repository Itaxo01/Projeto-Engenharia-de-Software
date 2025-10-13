function clickOnEnter(e, idButton) {
	var key;

	if (window.event)
		key = window.event.keyCode;
	else
		key = e.which;

	if (key == 13) {
		document.getElementById(idButton).click();
		return false;
	}
	return true;
}

function noSubmit(e) {
	var key;

	if (window.event)
		key = window.event.keyCode;
	else
		key = e.which;

	if (key == 13) {
		return false;
	}
	return true;
}

function isEnter(e) {
	var key;

	if (window.event)
		key = window.event.keyCode;
	else
		key = e.which;

	if (key == 13) {
		return true;
	}
	return false;
}

function passaCampo(e, idProximoCampo) {
	var key;

	if (window.event)
		key = window.event.keyCode;
	else
		key = e.which;

	if (key == 13) {
		document.getElementById(idProximoCampo).focus();
		return false;
	}
	return true;
}

function passaTabCampo(e, idTabPanel, idTab, nameTab, idProximoCampo) {
	var key;

	if (window.event)
		key = window.event.keyCode;
	else
		key = e.which;

	if (key == 13) {
		RichFaces.switchTab(idTabPanel, idTab, nameTab);
		document.getElementById(idProximoCampo).focus();
		return false;
	}
	return true;
}

function setCaretToEnd(e) {
	var control = $((e.target ? e.target : e.srcElement).id);
	if (control.createTextRange) {
		var range = control.createTextRange();
		range.collapse(false);
		range.select();
	} else if (control.setSelectionRange) {
		control.focus();
		var length = control.value.length;
		control.setSelectionRange(length, length);
	}
	control.selectionStart = control.selectionEnd = control.value.length;
}

function toogle(togObjId) {
	var togObj = document.getElementById(togObjId);

	if (togObj.style.display == 'block') {
		togObj.style.display = 'none';
	} else {
		togObj.style.display = 'block';
	}
}

function isIE6() {
	if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) { // test for MSIE x.x;
		var ieversion = new Number(RegExp.$1) // capture x.x portion and store
		// as a number
		if (ieversion >= 6 && ieversion <= 7)
			return true;

	}

}

function formataCEP(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if (tam <= 3)
		campo.value = vr;
	if (tam > 3)
		campo.value = vr.substr(0, tam - 3) + '-' + vr.substr(tam - 3, tam);
}

function formataData(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if (tam > 2 && tam < 5)
		campo.value = vr.substr(0, tam - 2) + '/' + vr.substr(tam - 2, tam);
	if (tam >= 5 && tam <= 10)
		campo.value = vr.substr(0, 2) + '/' + vr.substr(2, 2) + '/'
				+ vr.substr(4, 4);

	return true;

}

// limpa todos os caracteres especiais do campo solicitado
function filtraCampo(campo) {
	var s = "";
	var cp = "";
	vr = campo.value;
	tam = vr.length;
	for (i = 0; i < tam; i++) {
		if (vr.substring(i, i + 1) != "/" && vr.substring(i, i + 1) != "-"
				&& vr.substring(i, i + 1) != "."
				&& vr.substring(i, i + 1) != ",") {
			s = s + vr.substring(i, i + 1);
		}
	}
	campo.value = s;
	return cp = campo.value
}

function formataCPF(campo) {
	// 04211423916
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;
	if (tam <= 2) {
		campo.value = vr;
	}
	if (tam > 2 && tam <= 5) {
		campo.value = vr.substr(0, tam - 2) + '-' + vr.substr(tam - 2, tam);
	}
	if (tam >= 6 && tam <= 8) {
		campo.value = vr.substr(0, tam - 5) + '.' + vr.substr(tam - 5, 3) + '-'
				+ vr.substr(tam - 2, tam);
	}
	if (tam >= 9 && tam <= 11) {
		campo.value = vr.substr(0, tam - 8) + '.' + vr.substr(tam - 8, 3) + '.'
				+ vr.substr(tam - 5, 3) + '-' + vr.substr(tam - 2, tam);
	}

}

function formataCNPJ(campo) {
	// 83.899.526/0001-82
	// 83 899 526 000182

	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;
	if (tam <= 2) {
		campo.value = vr;
	}

	if ((tam > 2) && (tam <= 5))
		campo.value = vr.substr(0, 2) + '.' + vr.substr(2, tam);

	if ((tam >= 6) && (tam <= 8))
		campo.value = vr.substr(0, 2) + '.' + vr.substr(2, 3) + '.'
				+ vr.substr(5, 3) + '/';

	if ((tam >= 9) && (tam <= 14))
		campo.value = vr.substr(0, 2) + '.' + vr.substr(2, 3) + '.'
				+ vr.substr(5, 3) + '/' + vr.substr(8, 4) + '-'
				+ vr.substr(12, 2);

	return true;
}

function formataNumeroProcesso(evt, campo) {
	// 007923/1909-91

	var charCode = (evt.which) ? evt.which : evt.keyCode
	if (charCode > 31 && (charCode < 48 || charCode > 57))
		return false;

	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;
	if (tam <= 6) {
		campo.value = vr;
	}

	if ((tam > 6) && (tam <= 10))
		campo.value = vr.substr(0, 6) + '/' + vr.substr(6, 4);

	if ((tam > 10) && (tam <= 12))
		campo.value = vr.substr(0, 6) + '/' + vr.substr(6, 4) + '-'
				+ vr.substr(10, 2);

	return true;

}

function formataNumerico(evt) {
	try {
		var charCode = (evt.which) ? evt.which : evt.keyCode;
				if (charCode > 31 && (charCode < 48 || charCode > 57))
					return false;

	} catch (e) {
		return false;
	}

	return true;
}

function formataValor(campo) {
	campo.value = filtraCampo(campo);
	vr = campo.value;
	tam = vr.length;

	if (tam <= 2) {
		campo.value = vr;
	}
	if ((tam > 2) && (tam <= 5)) {
		campo.value = vr.substr(0, tam - 2) + ',' + vr.substr(tam - 2, tam);
	}
	if ((tam >= 6) && (tam <= 8)) {
		campo.value = vr.substr(0, tam - 5) + '.' + vr.substr(tam - 5, 3) + ','
				+ vr.substr(tam - 2, tam);
	}
	if ((tam >= 9) && (tam <= 11)) {
		campo.value = vr.substr(0, tam - 8) + '.' + vr.substr(tam - 8, 3) + '.'
				+ vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam);
	}
	if ((tam >= 12) && (tam <= 14)) {
		campo.value = vr.substr(0, tam - 11) + '.' + vr.substr(tam - 11, 3)
				+ '.' + vr.substr(tam - 8, 3) + '.' + vr.substr(tam - 5, 3)
				+ ',' + vr.substr(tam - 2, tam);
	}
	if ((tam >= 15) && (tam <= 18)) {
		campo.value = vr.substr(0, tam - 14) + '.' + vr.substr(tam - 14, 3)
				+ '.' + vr.substr(tam - 11, 3) + '.' + vr.substr(tam - 8, 3)
				+ '.' + vr.substr(tam - 5, 3) + ',' + vr.substr(tam - 2, tam);
	}

	return true;

}

function mudaBotoes(bid1, bid2) {
	document.getElementById(bid1).style.display = 'none';
	document.getElementById(bid2).style.display = '';
}

function formataMascara(objeto, sMask, evtKeyPress) {
	var i, nCount, sValue, fldLen, mskLen, bolMask, sCod, nTecla;

	if (document.all) { // Internet Explorer
		nTecla = evtKeyPress.keyCode;
	} else if (document.layers) { // Nestcape
		nTecla = evtKeyPress.which;
	} else {
		nTecla = evtKeyPress.which;
		if (nTecla == 8) {
			return true;
		}
	}

	sValue = objeto.value;

	// Limpa todos os caracteres de formata��o que
	// j� estiverem no campo.
	sValue = sValue.toString().replace("-", "");
	sValue = sValue.toString().replace("-", "");
	sValue = sValue.toString().replace(".", "");
	sValue = sValue.toString().replace(".", "");
	sValue = sValue.toString().replace("/", "");
	sValue = sValue.toString().replace("/", "");
	sValue = sValue.toString().replace(":", "");
	sValue = sValue.toString().replace(":", "");
	sValue = sValue.toString().replace("(", "");
	sValue = sValue.toString().replace("(", "");
	sValue = sValue.toString().replace(")", "");
	sValue = sValue.toString().replace(")", "");
	sValue = sValue.toString().replace(" ", "");
	sValue = sValue.toString().replace(" ", "");
	fldLen = sValue.length;
	mskLen = sMask.length;

	i = 0;
	nCount = 0;
	sCod = "";
	mskLen = fldLen;

	while (i <= mskLen) {
		bolMask = ((sMask.charAt(i) == "-") || (sMask.charAt(i) == ".")
				|| (sMask.charAt(i) == "/") || (sMask.charAt(i) == ":"))
		bolMask = bolMask
				|| ((sMask.charAt(i) == "(") || (sMask.charAt(i) == ")") || (sMask
						.charAt(i) == " "))

		if (bolMask) {
			sCod += sMask.charAt(i);
			mskLen++;
		} else {
			sCod += sValue.charAt(nCount);
			nCount++;
		}

		i++;
	}

	objeto.value = sCod;

	if (nTecla != 8) { // backspace
		if (sMask.charAt(i - 1) == "9") { // apenas n�meros...
			return ((nTecla > 47) && (nTecla < 58));
		} else { // qualquer caracter...
			return true;
		}
	} else {
		return true;
	}
}

function formataTelefone(event,maxLength) {

	event = event || window.event;
	var campo = event.target || event.srcElement;

	var charCode = event.which || event.keyCode;

	if(charCode == 8 || charCode == 46 || (charCode >= 36 && charCode <= 40) ) {
		return false;
	}

	var vr = campo.value;

	vr = vr.replace("(", "");
	vr = vr.replace(")", "");
	vr = vr.replace("-", "");
	vr = vr.replace("+", "");
	vr = vr.replace(" ", "");
	vr = vr.replace(" ", "");

	if (maxLength != null)
		vr = vr.substring(0, maxLength-1);

	tam = vr.length;

	if (tam >= 5 && tam <= 8)
		campo.value = vr.substr(0, 4) + '-' + vr.substr(4, tam);

	if (tam == 9)
		campo.value = vr.substr(0, 5) + '-' + vr.substr(5, tam);

	if (tam == 10)
		campo.value = '(' + vr.substr(0, 2) + ')' + ' ' + vr.substr(2, 4) + '-' + vr.substr(6, tam);

	if (tam == 11)
		campo.value = '(' + vr.substr(0, 2) + ')' + ' ' + vr.substr(2, 5) + '-' + vr.substr(7, tam);

	if (tam == 12)
		campo.value = '+' + vr.substr(0, 2)+ ' ' + '(' + vr.substr(2, 2) + ')' + ' ' + vr.substr(4, 4) + '-' + vr.substr(8, tam);

	if (tam >= 13)
		campo.value = '+' + vr.substr(0, 2)+ ' ' + '(' + vr.substr(2, 2) + ')' + ' ' + vr.substr(4, 5) + '-' + vr.substr(9, tam);

	return true;
}

/*
 * function saltaCampo(campo,tamanhoMaximo,indice,evt){ var vr = campo.value;
 * var tam = vr.length; var elements = document.forms.aapf.elements; if
 * (tam>=tamanhoMaximo && typeof(elements[indice])!='undefined'){
 * //elements[indice].focus(); for (i=0;i<elements.length;i++) { if
 * (elements[i].tabIndex==indice+1){ elements[i].focus(); } } } }
 */
