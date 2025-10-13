/**
 * Script interno para carregar fotos com atributo data-src
 * Substitui a dependência do script externo img-data-src
 */
(function() {
    'use strict';
    
    /**
     * Processa todas as imagens com atributo data-src
     */
    function loadDataSrcImages() {
        // Busca todas as imagens com atributo data-src
        var images = document.querySelectorAll('img[data-src]');
        
        images.forEach(function(img) {
            var dataSrc = img.getAttribute('data-src');
            
            if (dataSrc && dataSrc.trim() !== '') {
                // Cria uma nova imagem para testar o carregamento
                var testImage = new Image();
                
                testImage.onload = function() {
                    // Se a imagem carregar com sucesso, atualiza o src
                    img.src = dataSrc;
                    img.removeAttribute('data-src');
                };
                
                testImage.onerror = function() {
                    // Se houver erro, mantém a imagem padrão
                    console.warn('Erro ao carregar foto:', dataSrc);
                    // Remove o data-src para evitar reprocessamento
                    img.removeAttribute('data-src');
                };
                
                // Inicia o carregamento da imagem de teste
                testImage.src = dataSrc;
            }
        });
    }
    
    /**
     * Observa mudanças no DOM para processar novas imagens adicionadas dinamicamente
     */
    function observeDOMChanges() {
        // Configuração do observer
        var observerConfig = {
            childList: true,
            subtree: true
        };
        
        // Cria o observer
        var observer = new MutationObserver(function(mutations) {
            var hasNewImages = false;
            
            mutations.forEach(function(mutation) {
                if (mutation.addedNodes.length > 0) {
                    mutation.addedNodes.forEach(function(node) {
                        if (node.nodeType === 1) { // Element node
                            if (node.tagName === 'IMG' && node.hasAttribute('data-src')) {
                                hasNewImages = true;
                            } else if (node.querySelectorAll) {
                                var imgs = node.querySelectorAll('img[data-src]');
                                if (imgs.length > 0) {
                                    hasNewImages = true;
                                }
                            }
                        }
                    });
                }
            });
            
            if (hasNewImages) {
                // Aguarda um momento para garantir que o DOM esteja estável
                setTimeout(loadDataSrcImages, 100);
            }
        });
        
        // Inicia a observação
        observer.observe(document.body, observerConfig);
    }
    
    /**
     * Inicialização
     */
    function init() {
        // Processa imagens existentes
        loadDataSrcImages();
        
        // Configura observer para mudanças futuras
        observeDOMChanges();
        
        // Reprocessa após AJAX requests (compatibilidade com RichFaces)
        if (window.A4J && window.A4J.AJAX) {
            var originalOnComplete = window.A4J.AJAX.onComplete;
            window.A4J.AJAX.onComplete = function() {
                if (originalOnComplete) {
                    originalOnComplete.apply(this, arguments);
                }
                setTimeout(loadDataSrcImages, 100);
            };
        }
    }
    
    // Aguarda o carregamento do DOM
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        // DOM já carregado
        init();
    }
    
    // Exporta função para uso manual se necessário
    window.loadDataSrcImages = loadDataSrcImages;
})();