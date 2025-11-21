/**
 * Loading State Utilities
 * Consistent loading patterns across the application
 */

const LoadingUtils = {
    /**
     * Show button loading state
     */
    buttonLoading(button, loadingText = null) {
        if (!button) return;
        
        button.disabled = true;
        button.classList.add('btn-loading');
        
        if (loadingText && !button.dataset.originalText) {
            button.dataset.originalText = button.textContent;
            button.querySelector('.btn-text')?.remove();
            button.innerHTML = `<span class="btn-text">${loadingText}</span>`;
        }
    },

    /**
     * Reset button loading state
     */
    buttonReset(button) {
        if (!button) return;
        
        button.disabled = false;
        button.classList.remove('btn-loading');
        
        if (button.dataset.originalText) {
            button.textContent = button.dataset.originalText;
            delete button.dataset.originalText;
        }
    },

    /**
     * Show overlay loading on element
     */
    showOverlay(element, text = 'Carregando...') {
        if (!element) return;
        
        const overlay = document.createElement('div');
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="spinner"></div>
            <div class="loading-overlay-text">${text}</div>
        `;
        
        element.style.position = 'relative';
        element.appendChild(overlay);
        
        return overlay;
    },

    /**
     * Hide overlay loading
     */
    hideOverlay(element) {
        if (!element) return;
        
        const overlay = element.querySelector('.loading-overlay');
        if (overlay) {
            overlay.remove();
        }
    },

    /**
     * Create skeleton loader
     */
    createSkeleton(type = 'text', count = 3) {
        const container = document.createElement('div');
        
        for (let i = 0; i < count; i++) {
            const skeleton = document.createElement('div');
            skeleton.className = `skeleton skeleton-${type}`;
            container.appendChild(skeleton);
        }
        
        return container;
    },

    /**
     * Show inline loading (e.g., in search results)
     */
    showInlineLoading(container, text = 'Carregando...') {
        if (!container) return;
        
        const loading = document.createElement('div');
        loading.className = 'loading-inline';
        loading.innerHTML = `
            <div class="spinner spinner-small"></div>
            <span>${text}</span>
        `;
        
        container.innerHTML = '';
        container.appendChild(loading);
    },

    /**
     * Optimistic UI update
     */
    optimisticUpdate(element, updateFn, revertFn) {
        if (!element) return Promise.reject();
        
        element.classList.add('optimistic-pending');
        updateFn();
        
        return {
            success: () => {
                element.classList.remove('optimistic-pending');
                element.classList.add('optimistic-success');
                setTimeout(() => element.classList.remove('optimistic-success'), 500);
            },
            error: () => {
                element.classList.remove('optimistic-pending');
                element.classList.add('optimistic-error');
                revertFn();
                setTimeout(() => element.classList.remove('optimistic-error'), 500);
            }
        };
    },

    /**
     * Disable form during submission
     */
    disableForm(formElement) {
        if (!formElement) return;
        
        formElement.classList.add('form-disabled');
        const buttons = formElement.querySelectorAll('button, input[type="submit"]');
        buttons.forEach(btn => {
            btn.disabled = true;
            btn.classList.add('btn-loading');
        });
    },

    /**
     * Enable form after submission
     */
    enableForm(formElement) {
        if (!formElement) return;
        
        formElement.classList.remove('form-disabled');
        const buttons = formElement.querySelectorAll('button, input[type="submit"]');
        buttons.forEach(btn => {
            btn.disabled = false;
            btn.classList.remove('btn-loading');
        });
    },

    /**
     * Show page-wide loading overlay
     */
    showPageLoading(text = 'Carregando...') {
        const existing = document.querySelector('.page-loading');
        if (existing) return;
        
        const overlay = document.createElement('div');
        overlay.className = 'page-loading';
        overlay.innerHTML = `
            <div class="spinner spinner-large"></div>
            <div class="page-loading-text">${text}</div>
        `;
        
        document.body.appendChild(overlay);
    },

    /**
     * Hide page-wide loading overlay
     */
    hidePageLoading() {
        const overlay = document.querySelector('.page-loading');
        if (overlay) {
            overlay.remove();
        }
    }
};

// Make available globally
window.LoadingUtils = LoadingUtils;
