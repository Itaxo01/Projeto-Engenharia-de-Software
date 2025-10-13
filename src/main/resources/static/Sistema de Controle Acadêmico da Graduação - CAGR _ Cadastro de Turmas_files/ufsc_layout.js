function writeBarraSuperiorCAS(https, nomeServico, titulo, tituloStyle, subTitulo, subTituloStyle,tituloTrilha,servicoLink,showAutoContraste,showDiminuirFonte,showAumentarFonte, enderecoBarra) {
	var enderecoBarraDefault = '//sistemas.ufsc.br/inc/barra_cas';
//	enderecoBarraDefault = '//localhost:8443/ufscCAS2/inc/barra_cas';

	var first = true;
	
	if (nomeServico != null && nomeServico != "") {
		nomeServico = (first ? "?":"&") + 'nomeServico=' + encodeURIComponent(nomeServico);
		first = false;
	} else {
		nomeServico = "";
	}

	if (titulo != null && titulo != "") {
		titulo = (first ? "?":"&") + 'titulo=' + encodeURIComponent(titulo);
		first = false;
	} else {
		titulo = "";
	}

	if (subTitulo != null && subTitulo != "") {
		subTitulo = (first ? "?":"&") + 'subTitulo=' +  encodeURIComponent(subTitulo);
		first = false;
	} else {
		subTitulo = "";
	}
	
	if (tituloStyle != null && tituloStyle != "") {
		tituloStyle = (first ? "?":"&") + 'tituloStyle=' + encodeURIComponent(tituloStyle);
		first = false;
	} else {
		tituloStyle = "";
	}
	
	if (subTituloStyle != null && subTituloStyle != "") {
		subTituloStyle = (first ? "?":"&") + 'subTituloStyle=' + encodeURIComponent(subTituloStyle);
		first = false;
	} else {
		subTituloStyle = "";
	}
	
	if (tituloTrilha != null && tituloTrilha != "") {
		tituloTrilha = (first ? "?":"&") + 'tituloTrilha=' + encodeURIComponent(tituloTrilha);
		first = false;
	} else {
		tituloTrilha = "";
	}
		
	if (servicoLink != null && servicoLink != "") {
		servicoLink = (first ? "?":"&") + 'servicoLink=' + encodeURIComponent(servicoLink);
		first = false;
	} else {
		servicoLink = "";
	}
	
	var iframe = '<div style="display: block; height: auto;"><iframe src="'
			+ enderecoBarraDefault
			+ nomeServico
			+ titulo
			+ tituloStyle
			+ subTitulo
			+ subTituloStyle
			+ tituloTrilha
			+ servicoLink
			+ '" marginheight="0" marginwidth="0" width="100%" height="180px" scrolling="no" style="border: 0px; padding: 0px; margin: 0px" frameborder="0"></iframe></div>';

	document.write(iframe);
}
