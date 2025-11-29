// ============================================
// AVALIATION HANDLER JS
// Responsável por lidar com avaliações (ratings):
// Estrelas interativas, médias e cálculos
// ============================================

console.log('✅ avaliationHandler.js carregado');

// ============================================
// INTERACTIVE STARS SETUP
// ============================================

// ✅ Make stars clickable for rating submission
function setupInteractiveStars() {
    // Discipline stars
    const disciplineStars = document.querySelector('.discipline-rating-stars');
    if (disciplineStars) {
        makeStarsInteractive(disciplineStars, null); // null = disciplina (sem professor)
    }
    
    // Professor stars
    const professorItems = document.querySelectorAll('.professor-item');
    professorItems.forEach((item, index) => {
        const starsContainer = item.querySelector('.rating-stars');
        if (starsContainer && PROFESSORES_DATA && PROFESSORES_DATA[index]) {
            const prof = PROFESSORES_DATA[index];
            const profId = prof.id || prof.professorId || prof.ID || index;
            makeStarsInteractive(starsContainer, profId);
        }
    });
}

function makeStarsInteractive(starsContainer, professorId) {
    // ✅ FIX: Remove existing listeners by cloning to prevent duplicate submissions
    starsContainer.querySelectorAll('.star').forEach((oldStar, index) => {
        const newStar = oldStar.cloneNode(true);
        oldStar.parentNode.replaceChild(newStar, oldStar);
    });
    
    // Re-query stars after cloning
    const freshStars = starsContainer.querySelectorAll('.star');
    
    // Get initial average rating to display
    const avgRating = parseFloat(starsContainer.closest('.discipline-rating, .professor-rating')
        ?.querySelector('.rating-value, .rating-score')?.textContent) || 0;
    
    // Check if current user has voted
    const userRating = getUserCurrentRating(professorId);
    const hasUserVoted = userRating !== null;
    
    // Display the average rating on load
    if (avgRating > 0) {
        freshStars.forEach((star, index) => {
            star.classList.remove('filled', 'half', 'hover', 'user-voted', 'half-user-voted');
            if (avgRating >= index + 1) {
                star.classList.add('filled');
                // Add user-voted class if user has voted
                if (hasUserVoted) {
                    star.classList.add('user-voted');
                }
            } else if (avgRating >= index + 0.5) {
                // Add appropriate half class based on whether user voted
                if (hasUserVoted) {
                    star.classList.add('half-user-voted');
                } else {
                    star.classList.add('half');
                }
            }
        });
    }
    
    // ✅ REMOVED: Direct star clicking - now only visual display
    // Stars are no longer clickable; users must use the "Adicionar avaliação" link
    freshStars.forEach(star => {
        star.style.cursor = 'default'; // Change cursor to indicate not clickable
    });
}

function highlightStars(stars, rating) {
    stars.forEach((star, index) => {
        // Remove all state classes first
        star.classList.remove('hover', 'half', 'filled', 'user-voted', 'half-user-voted');
        
        if (index < rating) {
            star.classList.add('hover');
        }
    });
}

function clearStarsHighlight(stars) {
    stars.forEach(star => {
        star.classList.remove('hover', 'half', 'filled', 'half-user-voted', 'user-voted');
    });
}

// ============================================
// RATING UTILITIES
// ============================================

function getUserCurrentRating(professorId) {
    const avaliacao = AVALIACOES_DATA.find(a => {
        return a.isOwner && (String(a.professorId || '') === String(professorId || ''));
    });

    console.log('Avaliação do usuário para professorId', professorId, ':', avaliacao?.nota);
    return avaliacao?.nota || null;
}

// Calcular média e total
function calcularStats(avaliacoes) {
    const comNota = avaliacoes.filter(a => a.nota > 0);
    if (comNota.length === 0) return { media: 0, total: 0 };
    
    const soma = comNota.reduce((acc, a) => acc + a.nota, 0);
    return { media: soma / comNota.length, total: comNota.length };
}

// ============================================
// UPDATE UI FUNCTIONS
// ============================================

// Atualizar header da disciplina
function atualizarDisciplina(stats, comentarios) {
    document.querySelector('.discipline-rating .rating-score').textContent = 
        stats.total > 0 ? stats.media.toFixed(1) : 'N/A';
    
    const ratingCountElement = document.querySelector('.discipline-rating .rating-count');
    ratingCountElement.textContent = 
        `${stats.total} ${stats.total === 1 ? 'avaliação' : 'avaliações'}`;
    
    // Add "remove rating" link if user has voted
    addRemoveRatingLink(ratingCountElement, null);
    
    preencherEstrelas(document.querySelector('.discipline-rating .rating-stars'), stats.media);
    
    // Mostrar comentários da disciplina
    mostrarComentarios(comentarios, null);
}

// Atualizar professor específico
function atualizarProfessor(index, stats) {
    const professorItem = document.querySelectorAll('.professor-item')[index];
    if (!professorItem) return;
    
    professorItem.querySelector('.rating-value').textContent = 
        stats.total > 0 ? stats.media.toFixed(1) : 'N/A';
    
    const ratingCountElement = professorItem.querySelector('.rating-count');
    ratingCountElement.textContent = 
        `${stats.total} ${stats.total === 1 ? 'avaliação' : 'avaliações'}`;
    
    // Add "remove rating" link if user has voted for this professor
    const prof = PROFESSORES_DATA[index];
    const profId = prof?.id || prof?.professorId || prof?.ID || index;
    addRemoveRatingLink(ratingCountElement, profId);
    
    preencherEstrelas(professorItem.querySelector('.rating-stars'), stats.media);
}

// Preencher estrelas
function preencherEstrelas(container, nota) {
    const stars = container.querySelectorAll('.star');
    stars.forEach((star, i) => {
        star.classList.remove('filled', 'half');
        if (nota >= i + 1) star.classList.add('filled');
        else if (nota >= i + 0.5) star.classList.add('half');
    });
}

// ============================================
// RATING MODAL & SUBMISSION
// ============================================

/**
 * Add "remove rating" or "add rating" link below rating count
 */
function addRemoveRatingLink(ratingCountElement, professorId) {
    // Check if user has voted for this context
    const userRating = getUserCurrentRating(professorId);
    
    // Remove existing link if present
    const existingLink = ratingCountElement.parentElement.querySelector('.remove-rating-link, .add-rating-link');
    if (existingLink) {
        existingLink.remove();
    }
    
    if (userRating !== null) {
        // User has rated - show remove link
        const removeLink = document.createElement('div');
        removeLink.className = 'remove-rating-link';
        // ✅ FIX: Use quotes to preserve professorId as string (prevents "0882497234989588" from becoming 882497234989588)
        removeLink.innerHTML = `<span onclick="removeRating(event, '${professorId}')">Remover minha avaliação</span>`;
        ratingCountElement.parentElement.appendChild(removeLink);
    } else {
        // User hasn't rated - show add link
        const addLink = document.createElement('div');
        addLink.className = 'add-rating-link';
        addLink.innerHTML = `<span onclick="openRatingModal('${professorId}')">Adicionar avaliação</span>`;
        ratingCountElement.parentElement.appendChild(addLink);
    }
}

/**
 * Remove user's rating
 */
async function removeRating(event, professorId = null) {
    
    if (!confirm('Tem certeza que deseja remover sua avaliação?')) {
        return;
    }
    
    // Find the clicked element and its parent link
    const clickedSpan = event?.target;
    const removeLink = clickedSpan?.closest('.remove-rating-link');
    
    // Find the rating section based on context
    let ratingSection = null;
    if (professorId && professorId !== 'null') {
        // For professor rating - find the professor card
        const professorCards = document.querySelectorAll('.professor-item');
        for (let card of professorCards) {
            const profData = PROFESSORES_DATA[Array.from(professorCards).indexOf(card)];
            if (profData) {
                const profId = String(profData.id || profData.professorId || profData.ID || '');
                if (profId === String(professorId)) {
                    ratingSection = card.querySelector('.professor-rating');
                    break;
                }
            }
        }
    } else {
        // For discipline rating
        ratingSection = document.querySelector('.discipline-rating');
    }
    
    // Disable the link immediately to prevent double-clicks
    if (removeLink) {
        removeLink.style.pointerEvents = 'none';
        removeLink.style.opacity = '0.6';
        const originalHtml = clickedSpan.innerHTML;
        clickedSpan.dataset.originalHtml = originalHtml;
        clickedSpan.innerHTML = '<span class="loading-inline" style="display: inline-flex; align-items: center; gap: 4px;"><div class="spinner spinner-small"></div><span>Removendo...</span></span>';
    }
    
    // Add subtle visual feedback to rating section (no overlay)
    if (ratingSection) {
        ratingSection.style.opacity = '0.7';
        ratingSection.style.transition = 'opacity 0.2s';
    }
    
    try {
        const formData = new FormData();
        formData.append('disciplinaId', CLASS_ID);
        if (professorId && professorId !== 'null') {
            formData.append('professorId', professorId);
        }
        
        const response = await fetch('/api/avaliacao/rating/delete', {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            
            // Restore UI on error
            if (removeLink && clickedSpan.dataset.originalHtml) {
                removeLink.style.pointerEvents = '';
                removeLink.style.opacity = '';
                clickedSpan.innerHTML = clickedSpan.dataset.originalHtml;
            }
            if (ratingSection) {
                ratingSection.style.opacity = '';
            }
            
            showToast(parseErrorMessage(errorText) || 'Erro ao remover avaliação', 'error');
            return;
        }
        
        console.log('Avaliação removida com sucesso');
        
        // Keep loading state while page reloads
        // Reload page to show updated ratings
        window.location.reload();
        
    } catch (error) {
        console.error('Erro ao remover avaliação:', error);
        
        // Restore UI on error
        if (removeLink && clickedSpan.dataset.originalHtml) {
            removeLink.style.pointerEvents = '';
            removeLink.style.opacity = '';
            clickedSpan.innerHTML = clickedSpan.dataset.originalHtml;
        }
        if (ratingSection) {
            ratingSection.style.opacity = '';
        }
        
        showToast(parseErrorMessage(error.message) || 'Erro ao remover avaliação', 'error');
    }
}

/**
 * Open rating modal for user to select stars
 */
function openRatingModal(professorId = null) {
    // Create modal if it doesn't exist
    let modal = document.getElementById('ratingModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'ratingModal';
        modal.className = 'rating-modal';
        modal.innerHTML = `
            <div class="rating-modal-content">
                <span class="rating-modal-close" onclick="closeRatingModal()">&times;</span>
                <h3 id="ratingModalTitle">Avaliar</h3>
                <p id="ratingModalSubtitle">Selecione sua nota:</p>
                <div class="rating-modal-stars" id="ratingModalStars">
                    <span class="modal-star" data-rating="1">★</span>
                    <span class="modal-star" data-rating="2">★</span>
                    <span class="modal-star" data-rating="3">★</span>
                    <span class="modal-star" data-rating="4">★</span>
                    <span class="modal-star" data-rating="5">★</span>
                </div>
                <div class="rating-modal-actions">
                    <button class="btn-modal-cancel" onclick="closeRatingModal()">Cancelar</button>
                    <button class="btn-modal-submit" id="btnModalSubmitRating" disabled onclick="submitModalRating()">Confirmar</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
        
        // Close on click outside
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeRatingModal();
            }
        });
    }
    
    // Store the professor ID for later use
    modal.dataset.professorId = professorId || '';
    
    // Update title based on context
    const titleElement = document.getElementById('ratingModalTitle');
    if (professorId !== null && professorId !== 'null' && PROFESSORES_DATA) {
        const prof = PROFESSORES_DATA.find(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorId);
        });
        const profNome = prof?.nome || prof?.name || 'Professor';
        titleElement.textContent = `Avaliar ${profNome}`;
    } else {
        titleElement.textContent = 'Avaliar Disciplina';
    }
    
    // Reset stars
    const modalStars = document.querySelectorAll('.modal-star');
    modalStars.forEach(star => {
        star.classList.remove('selected', 'hover');
        
        // Add hover and click events
        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.dataset.rating);
            highlightModalStars(rating);
        });
        
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            selectModalRating(rating);
        });
    });
    
    // Reset on mouse leave
    const starsContainer = document.getElementById('ratingModalStars');
    starsContainer.addEventListener('mouseleave', function() {
        const selectedRating = modal.dataset.selectedRating;
        if (selectedRating) {
            highlightModalStars(parseInt(selectedRating));
        } else {
            clearModalStars();
        }
    });
    
    // Reset selection
    delete modal.dataset.selectedRating;
    document.getElementById('btnModalSubmitRating').disabled = true;
    
    // Show modal
    modal.style.display = 'flex';
}

/**
 * Close rating modal
 */
function closeRatingModal() {
    const modal = document.getElementById('ratingModal');
    if (modal) {
        modal.style.display = 'none';
        delete modal.dataset.selectedRating;
        delete modal.dataset.professorId;
        clearModalStars();
    }
}

/**
 * Highlight modal stars on hover
 */
function highlightModalStars(rating) {
    const stars = document.querySelectorAll('.modal-star');
    stars.forEach((star, index) => {
        star.classList.remove('hover');
        if (index < rating) {
            star.classList.add('hover');
        }
    });
}

/**
 * Clear modal stars highlighting
 */
function clearModalStars() {
    const stars = document.querySelectorAll('.modal-star');
    stars.forEach(star => {
        star.classList.remove('hover', 'selected');
    });
}

/**
 * Select a rating in the modal
 */
function selectModalRating(rating) {
    const modal = document.getElementById('ratingModal');
    modal.dataset.selectedRating = rating;
    
    // Update stars to show selection
    const stars = document.querySelectorAll('.modal-star');
    stars.forEach((star, index) => {
        star.classList.remove('selected', 'hover');
        if (index < rating) {
            star.classList.add('selected');
        }
    });
    
    // Enable submit button
    document.getElementById('btnModalSubmitRating').disabled = false;
}

/**
 * Submit the rating from modal
 */
async function submitModalRating() {
    const modal = document.getElementById('ratingModal');
    const rating = parseInt(modal.dataset.selectedRating);
    const professorId = modal.dataset.professorId;
    
    if (!rating || rating < 1 || rating > 5) {
        showToast('Por favor, selecione uma avaliação.', 'warning');
        return;
    }
    
    // Don't close modal yet - show loading state
    const modalContent = modal.querySelector('.modal-content');
    const submitButton = document.getElementById('btnModalSubmitRating');
    
    // Add loading overlay to modal
    if (modalContent && !modalContent.querySelector('.loading-overlay')) {
        const overlay = document.createElement('div');
        overlay.className = 'loading-overlay';
        overlay.innerHTML = `
            <div class="spinner"></div>
            <div class="loading-overlay-text">Enviando avaliação...</div>
        `;
        modalContent.style.position = 'relative';
        modalContent.appendChild(overlay);
    }
    
    // Disable submit button
    if (submitButton) {
        submitButton.disabled = true;
        submitButton.classList.add('btn-loading');
    }
    
    // Submit rating
    await submitRating(rating, professorId === '' ? null : professorId);
}

/**
 * Atualizar visualização após mudanças
 */
function atualizarVisualizacao() {
    // Atualizar visualização do professor selecionado (comentários agora são apenas para professores)
    if (professorSelecionado !== null) {
        const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
        const avaliacoesProf = avaliacoesProfessores.filter(a => 
            String(a.professorId) === String(professorSelecionado)
        );
        const stats = calcularStats(avaliacoesProf);
        
        // Filtrar comentários do professor
        const comentariosProfessor = allComments.filter(c => 
            String(c.professorId) === String(professorSelecionado)
        );
        
        // Encontrar índice do professor
        const profIndex = PROFESSORES_DATA.findIndex(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorSelecionado);
        });
        
        if (profIndex !== -1) {
            atualizarProfessor(profIndex, stats);
            
            // Atualizar lista de comentários
            const prof = PROFESSORES_DATA[profIndex];
            const profNome = prof?.nome || prof?.name || 'Professor';
            mostrarComentarios(comentariosProfessor, profNome);
        }
    }
    
    // ✅ Re-setup interactive stars after updating visualization
    setupInteractiveStars();
}

/**
 * Submeter rating (estrelas clicáveis)
 */
async function submitRating(rating, professorId = null) {
    
    if (rating < 1 || rating > 5) {
        showToast('Nota inválida.', 'warning');
        return;
    }
    
    console.log(`Submetendo rating: ${rating} estrelas para ${professorId ? 'professor' : 'disciplina'}`);
    
    const submitButton = document.getElementById('btnModalSubmitRating');
    if (submitButton) {
        submitButton.disabled = true;
        submitButton.classList.add('btn-loading');
        submitButton.textContent = 'Enviando...';
    }
    
    try {
        const formData = new FormData();
        formData.append('nota', rating);
        formData.append('disciplinaId', CLASS_ID);
        if (professorId) {
            formData.append('professorId', professorId);
        }
        
        const response = await fetch('/api/avaliacao/rating', {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            
            // Remove loading overlay
            const modal = document.getElementById('ratingModal');
            const overlay = modal?.querySelector('.loading-overlay');
            if (overlay) overlay.remove();
            
            // Re-enable button
            if (submitButton) {
                submitButton.disabled = false;
                submitButton.classList.remove('btn-loading');
                submitButton.textContent = 'Confirmar';
            }
            
            showToast(parseErrorMessage(errorText) || 'Erro ao enviar avaliação', 'error');
            return;
        }
        
        const result = await response.json();
        console.log('Rating salvo:', result);
        
        // ✅ Simplified: Just reload the page after successful rating
        window.location.reload();
        
    } catch (error) {
        console.error('Erro ao enviar rating:', error);
        
        // Remove loading overlay
        const modal = document.getElementById('ratingModal');
        const overlay = modal?.querySelector('.loading-overlay');
        if (overlay) overlay.remove();
        
        // Re-enable button
        if (submitButton) {
            submitButton.disabled = false;
            submitButton.classList.remove('btn-loading');
            submitButton.textContent = 'Confirmar';
        }
        
        showToast(parseErrorMessage(error.message) || 'Erro ao enviar avaliação', 'error');
        return null;
    }
}
