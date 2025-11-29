/**
 * Página da Disciplina - JavaScript
 */

console.log('✅ class.js carregado');

// ============================================
// TOAST NOTIFICATION SYSTEM
// ============================================

/**
 * Show a toast notification
 * @param {string} message - The message to display
 * @param {string} type - 'success', 'error', 'warning', 'info'
 * @param {number} duration - Duration in ms (default 4000)
 */
function showToast(message, type = 'info', duration = 4000) {
    // Create toast container if it doesn't exist
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10001;
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-width: 400px;
        `;
        document.body.appendChild(container);
    }
    
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    // Icon based on type
    const icons = {
        success: '✓',
        error: '✕',
        warning: '⚠',
        info: 'ℹ'
    };
    
    // Colors based on type
    const colors = {
        success: { bg: '#d4edda', border: '#28a745', text: '#155724', icon: '#28a745' },
        error: { bg: '#f8d7da', border: '#dc3545', text: '#721c24', icon: '#dc3545' },
        warning: { bg: '#fff3cd', border: '#ffc107', text: '#856404', icon: '#856404' },
        info: { bg: '#d1ecf1', border: '#17a2b8', text: '#0c5460', icon: '#17a2b8' }
    };
    
    const color = colors[type] || colors.info;
    
    toast.style.cssText = `
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 14px 16px;
        background: ${color.bg};
        border: 1px solid ${color.border};
        border-left: 4px solid ${color.border};
        border-radius: 6px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        animation: slideInRight 0.3s ease-out;
        color: ${color.text};
        font-size: 14px;
        line-height: 1.5;
    `;
    
    toast.innerHTML = `
        <span style="font-size: 18px; font-weight: bold; color: ${color.icon}; flex-shrink: 0;">${icons[type]}</span>
        <span style="flex: 1; word-break: break-word;">${escapeHtmlForToast(message)}</span>
        <button onclick="this.parentElement.remove()" style="
            background: none;
            border: none;
            font-size: 18px;
            cursor: pointer;
            color: ${color.text};
            opacity: 0.7;
            padding: 0;
            line-height: 1;
            flex-shrink: 0;
        ">&times;</button>
    `;
    
    // Add CSS animation if not exists
    if (!document.getElementById('toast-styles')) {
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(100%);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
    
    container.appendChild(toast);
    
    // Auto remove after duration
    setTimeout(() => {
        toast.style.animation = 'slideOutRight 0.3s ease-out forwards';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

/**
 * Parse and extract clean error message from potentially HTML response
 * @param {string} text - The error text (may be HTML or plain text)
 * @returns {string} - Clean error message
 */
function parseErrorMessage(text) {
    if (!text) return 'Erro desconhecido';
    
    // If it looks like HTML (contains tags)
    if (text.includes('<html') || text.includes('<body') || text.includes('<!DOCTYPE')) {
        // Try to extract meaningful error from common patterns
        
        // Try to find error in <h1> or <title>
        const h1Match = text.match(/<h1[^>]*>([^<]+)<\/h1>/i);
        if (h1Match) return h1Match[1].trim();
        
        const titleMatch = text.match(/<title[^>]*>([^<]+)<\/title>/i);
        if (titleMatch) return titleMatch[1].trim();
        
        // Try to find "message" in JSON-like content
        const messageMatch = text.match(/"message"\s*:\s*"([^"]+)"/);
        if (messageMatch) return messageMatch[1];
        
        // Try to find common Spring Boot error patterns
        const errorMatch = text.match(/There was an unexpected error[^<]*/i);
        if (errorMatch) return 'Ocorreu um erro interno no servidor';
        
        // Generic fallback for HTML
        return 'Ocorreu um erro no servidor. Tente novamente.';
    }
    
    // If it's JSON, try to parse it
    try {
        const json = JSON.parse(text);
        return json.message || json.error || text;
    } catch {
        // Not JSON, return as is (but truncate if too long)
        if (text.length > 200) {
            return text.substring(0, 200) + '...';
        }
        return text;
    }
}

/**
 * Escape HTML for display in toast (prevent XSS)
 */
function escapeHtmlForToast(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

let professorSelecionado = null;
let selectedFiles = [];
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
let isAdmin = false; // Flag de admin
let editingCommentId = null; // Track if we're editing a comment (inline)
let inlineEditFiles = []; // Files for inline editing (new files to upload)
let inlineEditExistingFiles = []; // Existing files to keep during edit
let replyingToCommentId = null; // Track if we're replying to a comment
let allComments = []

function generateListAllComments(listaComentario, lista) {
    listaComentario.forEach(comentario => {
        buscaFilho(comentario, lista)
    });
}

function buscaFilho (comentario, lista) {
    if (comentario.filhos.length > 0) {
        comentario.filhos.forEach(filho => {
            buscaFilho(filho, lista);
        });
    }

    lista.push(comentario);
}

document.addEventListener('DOMContentLoaded', function() {
    if (typeof AVALIACOES_DATA === 'undefined') return;
    if (typeof COMENTARIOS_DATA === 'undefined') {
        console.error('COMENTARIOS_DATA não está definido!');
        return;
    }
    
    // Obter email do usuário logado e status de admin
    isAdmin = typeof IS_ADMIN !== 'undefined' ? IS_ADMIN : false;
    const hasProfessors = typeof HAS_PROFESSORS !== 'undefined' ? HAS_PROFESSORS : false;

    generateListAllComments(COMENTARIOS_DATA, allComments)

    console.log('📊 Dados carregados:', {
        avaliacoes: AVALIACOES_DATA.length,
        comentarios: COMENTARIOS_DATA.length,
        listaComentarios: allComments.length,
        professores: PROFESSORES_DATA?.length || 0,
        isAdmin: isAdmin,
        hasProfessors: hasProfessors

    });

	//  AVALIACOES_DATA.forEach(a => {
	// 	console.log('Avaliação:', a);
	//  });

	//  COMENTARIOS_DATA.forEach(c => {
	// 	console.log('Comentário:', c);
	//  });
    
    // Separar avaliações
    const avaliacoesDisciplina = AVALIACOES_DATA.filter(a => !a.professorId);
    const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
    
    // Atualizar disciplina com estatísticas (sem comentários - agora só para professores)
    const statsDisciplina = calcularStats(avaliacoesDisciplina);
    atualizarDisciplina(statsDisciplina, []);
    
    // Atualizar professores e adicionar event listeners
    if (typeof PROFESSORES_DATA !== 'undefined' && hasProfessors) {
        console.log('👨‍🏫 Professores:', PROFESSORES_DATA);
        
        PROFESSORES_DATA.forEach((prof, index) => {
            console.log(`Professor ${index}:`, prof);
            
            // Identificar qual propriedade contém o ID (pode ser 'id', 'professorId', ou outra)
            const profId = prof.id || prof.professorId || prof.ID || index;
            const profNome = prof.nome || prof.name || 'Professor';
            
            const avaliacoesProf = avaliacoesProfessores.filter(a => 
                String(a.professorId) === String(profId)
            );
            const stats = calcularStats(avaliacoesProf);
            atualizarProfessor(index, stats);
            
            // Adicionar click listener
            const professorItem = document.querySelectorAll('.professor-item')[index];
            if (professorItem) {
                professorItem.addEventListener('click', () => {
                    // ✅ Não permitir desselecionar - apenas trocar de professor
                    if (String(professorSelecionado) !== String(profId)) {
                        console.log('Alternando para professor:', profId, profNome);
                        selecionarProfessor(profId, profNome);
                    }
                });
            }
        });
        
        // ✅ Selecionar primeiro professor automaticamente se nenhum estiver salvo
        let professorRestaurado = false;
        
        // Tentar restaurar professor selecionado do localStorage
        if (typeof CLASS_ID !== 'undefined') {
            const savedProfessor = localStorage.getItem(`selectedProfessor_${CLASS_ID}`);
            if (savedProfessor) {
                try {
                    const { id, nome } = JSON.parse(savedProfessor);
                    // Verificar se o professor ainda existe na lista
                    const profExists = PROFESSORES_DATA?.some(p => {
                        const profId = p.id || p.professorId || p.ID;
                        return String(profId) === String(id);
                    });
                    if (profExists) {
                        console.log('🔄 Restaurando professor salvo:', id, nome);
                        selecionarProfessor(id, nome);
                        professorRestaurado = true;
                    } else {
                        // Professor não existe mais, limpar localStorage
                        localStorage.removeItem(`selectedProfessor_${CLASS_ID}`);
                    }
                } catch (e) {
                    console.error('Erro ao restaurar professor:', e);
                    localStorage.removeItem(`selectedProfessor_${CLASS_ID}`);
                }
            }
        }
        
        // Se não restaurou nenhum professor, selecionar o primeiro
        if (!professorRestaurado && PROFESSORES_DATA.length > 0) {
            const firstProf = PROFESSORES_DATA[0];
            const firstProfId = firstProf.id || firstProf.professorId || firstProf.ID || 0;
            const firstProfNome = firstProf.nome || firstProf.name || 'Professor';
            console.log('📌 Selecionando primeiro professor:', firstProfId, firstProfNome);
            selecionarProfessor(firstProfId, firstProfNome);
        }
    } else {
        // ✅ Disciplina sem professores - exibir mensagem e desabilitar comentários
        console.log('⚠️ Disciplina sem professores cadastrados');
        mostrarInterfaceSemProfessores();
    }
    
    // Event listener no header da disciplina - não faz mais nada (não dá para desselecionar)
    const disciplineHeader = document.querySelector('.discipline-header');
    if (disciplineHeader) {
        disciplineHeader.style.cursor = 'default';
    }
    
    // Setup comment editor
    setupCommentEditor();
    
    // ✅ Setup interactive stars for discipline and professors
    setupInteractiveStars();
});

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

// Selecionar professor
function selecionarProfessor(professorId, professorNome) {
    console.log('Selecionando professor:', professorId, 'Atual:', professorSelecionado);
    
    // Atualizar professor selecionado
    professorSelecionado = professorId;
    
    // Salvar no localStorage para persistir entre recarregamentos
    if (typeof CLASS_ID !== 'undefined') {
        localStorage.setItem(`selectedProfessor_${CLASS_ID}`, JSON.stringify({ id: professorId, nome: professorNome }));
    }
    
    // ✅ FIX: Recalcular avaliações do professor dinamicamente do array global
    const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
    const avaliacoes = avaliacoesProfessores.filter(a => 
        String(a.professorId) === String(professorId)
    );
    
    console.log(`📊 Avaliações do professor ${professorNome}:`, avaliacoes.length);
    
    // Remover seleção anterior de TODOS os professores
    document.querySelectorAll('.professor-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // Encontrar e selecionar o professor correto
    const professorItems = Array.from(document.querySelectorAll('.professor-item'));
    professorItems.forEach((item, idx) => {
        // Obter ID do professor usando a mesma lógica de fallback
        const prof = PROFESSORES_DATA[idx];
        if (prof) {
            const profId = prof.id || prof.professorId || prof.ID || idx;
            if (String(profId) === String(professorId)) {
                item.classList.add('selected');
                console.log('✓ Professor selecionado no índice:', idx, 'ID:', profId);
            }
        }
    });
    
    const sectionTitle = document.querySelector('.section-header h2');
    if (sectionTitle) {
        sectionTitle.textContent = `Comentários para ${professorNome}`;
    }
    
    // ✅ Mostrar comentários do professor usando COMENTARIOS_DATA
    const comentariosProfessor = allComments.filter(c => 
        String(c.professorId) === String(professorId)
    );
    mostrarComentarios(comentariosProfessor, professorNome);
}

// ✅ Mostrar interface quando não há professores cadastrados
function mostrarInterfaceSemProfessores() {
    // Atualizar título da seção de comentários
    const sectionTitle = document.querySelector('.section-header h2');
    if (sectionTitle) {
        sectionTitle.textContent = 'Comentários';
    }
    
    // Esconder botão de adicionar comentário
    const addReviewBtn = document.querySelector('.btn-add-review');
    if (addReviewBtn) {
        addReviewBtn.style.display = 'none';
    }
    
    // Esconder editor de comentário
    const commentEditor = document.getElementById('commentEditor');
    if (commentEditor) {
        commentEditor.style.display = 'none';
    }
    
    // Mostrar mensagem na lista de comentários
    const commentsList = document.getElementById('commentsList');
    if (commentsList) {
        commentsList.innerHTML = `
            <div class="no-comments-message" style="text-align: center; padding: 40px 20px; color: var(--text-secondary);">
                <div style="font-size: 48px; margin-bottom: 16px;">📚</div>
                <h3 style="margin-bottom: 8px; color: var(--text-primary);">Disciplina sem professores cadastrados</h3>
                <p>Esta disciplina ainda não possui professores vinculados.</p>
                <p>Comentários só podem ser feitos para professores específicos.</p>
            </div>
        `;
    }
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

// Mostrar comentários
function mostrarComentarios(comentarios, professorNome) {
    const lista = document.querySelector('.reviews-list');
    
    if (comentarios.length === 0) {
        const entidade = professorNome || 'esta disciplina';
        lista.innerHTML = `<div class="no-reviews"><p>Ainda não há avaliações para ${entidade}.</p><p>Seja o primeiro a avaliar!</p></div>`;
        return;
    }
    
    lista.innerHTML = comentarios.filter(c => !c.comentarioPaiId && !c.deleted).map(comentario => {
        return renderCommentCard(comentario, false, 0);
    }).join('');
}

/**
 * Render a single comment card (parent or child) with recursive support for nested replies
 * @param {Object} comentario - The comment object
 * @param {Boolean} isChild - Whether this is a child comment
 * @param {Number} nestLevel - Depth level for nested styling
 */
function renderCommentCard(comentario, isChild = false, nestLevel = 0) {
    const isOwner = comentario.isOwner || false;
    const hasVoted = comentario.hasVoted; // 1 (upvote), -1 (downvote), 0 (no vote)
    
    // Classes para destacar botões de voto
    const upvoteClass = hasVoted === 1 ? 'voted' : '';
    const downvoteClass = hasVoted === -1 ? 'voted' : '';
    
    // ✅ Show delete button if user is owner OR admin
    const canDelete = isOwner || isAdmin;
    
    // ✅ Render attached files
    const arquivosHTML = comentario.arquivos && comentario.arquivos.length > 0 
        ? `<div class="comment-attachments">${renderArquivos(comentario.arquivos)}</div>`
        : '';
    
    // ✅ Check if comment was edited
    const isEdited = comentario.edited || false;
    const editedText = isEdited && comentario.editedAt 
        ? `<span class="edited-indicator"> (Editado em ${formatarData(comentario.editedAt)})</span>`
        : '';
    
    // Add child class for styling based on nesting level
    const childClass = isChild ? `child-comment nest-level-${nestLevel}` : 'parent-comment';
    
    // Render this comment card
    let html = `
    <div class="review-card ${childClass}" data-comment-id="${comentario.id}">
         <div class="review-header">
        <div class="reviewer-info">
             ${isChild ? '<span class="reply-indicator">↳ Resposta</span>' : ''}
             <div class="reviewer-avatar">${comentario.userInitials || '?'}</div>
             <span class="reviewer-date">${formatarData(comentario.createdAt)}${editedText}</span>
        </div>
         </div>
         <div class="review-content">${escapeHtml(comentario.texto)}</div>
         
         <!-- Arquivos Anexados -->
         ${arquivosHTML}
         
         <!-- Botões de Ação - Reddit Style -->
         <div class="review-actions">
            <div class="vote-container">
                <button class="vote-btn upvote-btn ${upvoteClass}" onclick="voteComment(${comentario.id}, true)" title="Upvote">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 19V5M5 12l7-7 7 7"/>
                    </svg>
                </button>
                <span class="vote-score" data-comment-id="${comentario.id}">${(comentario.upVotes || 0) - (comentario.downVotes || 0)}</span>
                <button class="vote-btn downvote-btn ${downvoteClass}" onclick="voteComment(${comentario.id}, false)" title="Downvote">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 5v14M5 12l7 7 7-7"/>
                    </svg>
                </button>
            </div>
            <div class="action-separator"></div>
            <button class="review-action-btn reply-btn" onclick="replyToComment(${comentario.id})">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                </svg>
                <span>responder</span>
            </button>
            ${isOwner ? `
            <button class="review-action-btn edit-btn" onclick="editComment(${comentario.id})">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                </svg>
                <span>editar</span>
            </button>
            ` : ''}
            ${canDelete ? `
            <button class="review-action-btn delete-btn" onclick="deleteComment(${comentario.id})">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                </svg>
                <span>deletar</span>
            </button>
            ` : ''}
         </div>
    </div>
    `;
    
    // Recursively render nested replies (filhos can have filhos)
    if (comentario.filhos && comentario.filhos.length > 0) {
        html += `<div class="child-comments-container nest-level-${nestLevel + 1}">`;
        comentario.filhos.filter(filho => !filho.deleted).forEach(filho => {
            html += renderCommentCard(filho, true, nestLevel + 1);
        });
        html += `</div>`;
    }
    
    return html;
}

// Formatar data
function formatarData(data) {
    if (!data) return 'Data desconhecida';
    const d = new Date(data);
    const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
    return `${meses[d.getMonth()]} ${d.getFullYear()}`;
}

/**
 * Render attached files (images and documents)
 */
function renderArquivos(arquivos) {
    if (!arquivos || arquivos.length === 0) return '';
    
    return arquivos.map(arquivo => {
        const isImage = arquivo.tipoMime && arquivo.tipoMime.startsWith('image/');
        const isPdf = arquivo.tipoMime === 'application/pdf';
        
        if (isImage) {
            // Image preview with click to enlarge
            return `
                <div class="attachment-item attachment-image">
                    <img src="/api/arquivos/${arquivo.id}" 
                         alt="${escapeHtml(arquivo.nomeOriginal)}"
                         onclick="openImageModal('/api/arquivos/${arquivo.id}', '${escapeHtml(arquivo.nomeOriginal)}')"
                         loading="lazy">
                    <div class="attachment-name">${escapeHtml(arquivo.nomeOriginal)}</div>
                </div>
            `;
        } else if (isPdf) {
            // PDF preview with click to view/download
            return `
                <div class="attachment-item attachment-pdf">
                    <div class="pdf-icon">📄</div>
                    <div class="attachment-info">
                        <div class="attachment-name">${escapeHtml(arquivo.nomeOriginal)}</div>
                        <div class="attachment-size">${formatFileSize(arquivo.tamanho)}</div>
                    </div>
                    <div class="attachment-actions">
                        <a href="/api/arquivos/${arquivo.id}" target="_blank" class="btn-view">Ver</a>
                        <a href="/api/arquivos/${arquivo.id}?download=true" class="btn-download">Baixar</a>
                    </div>
                </div>
            `;
        } else {
            // Other documents (Word, Excel, etc.) - download only
            const icon = getFileIcon(arquivo.nomeOriginal);
            return `
                <div class="attachment-item attachment-doc">
                    <div class="doc-icon">${icon}</div>
                    <div class="attachment-info">
                        <div class="attachment-name">${escapeHtml(arquivo.nomeOriginal)}</div>
                        <div class="attachment-size">${formatFileSize(arquivo.tamanho)}</div>
                    </div>
                    <a href="/api/arquivos/${arquivo.id}?download=true" class="btn-download">Baixar</a>
                </div>
            `;
        }
    }).join('');
}

/**
 * Open image in modal for full view
 */
function openImageModal(imageUrl, imageName) {
    // Create modal if it doesn't exist
    let modal = document.getElementById('imageModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'imageModal';
        modal.className = 'image-modal';
        modal.innerHTML = `
            <div class="image-modal-content">
                <span class="image-modal-close" onclick="closeImageModal()">&times;</span>
                <img id="modalImage" src="" alt="">
                <div id="modalImageName" class="modal-image-name"></div>
            </div>
        `;
        document.body.appendChild(modal);
        
        // Close on click outside
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeImageModal();
            }
        });
    }
    
    // Set image and show modal
    document.getElementById('modalImage').src = imageUrl;
    document.getElementById('modalImageName').textContent = imageName;
    modal.style.display = 'flex';
}

/**
 * Close image modal
 */
function closeImageModal() {
    const modal = document.getElementById('imageModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// ============================================
// COMMENT EDITOR FUNCTIONS
// ============================================

function setupCommentEditor() {
    const textarea = document.getElementById('commentText');
    const submitBtn = document.getElementById('submitBtn');
    
    if (textarea && submitBtn) {
        textarea.addEventListener('input', function() {
            updateSubmitButton();
        });
        
        // ✅ Add paste event listener for images and files
        textarea.addEventListener('paste', handlePaste);
    }
    
    // Also add paste listener to the entire comment form
    const commentForm = document.getElementById('commentForm');
    if (commentForm) {
        commentForm.addEventListener('paste', handlePaste);
    }
}

/**
 * Handle paste events to extract images from clipboard
 */
function handlePaste(event) {
    const items = event.clipboardData?.items;
    if (!items) return;
    
    let hasFiles = false;
    
    for (let i = 0; i < items.length; i++) {
        const item = items[i];
        
        // Check if item is a file
        if (item.kind === 'file') {
            const file = item.getAsFile();
            
            if (file) {
                // Check if it's an allowed file type
                const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
                const allowedMimeTypes = [
                    'image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp',
                    'application/pdf',
                    'application/msword',
                    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                    'application/vnd.ms-excel',
                    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                    'text/plain'
                ];
                
                const fileExtension = '.' + file.name.split('.').pop().toLowerCase();
                const isAllowedType = allowedMimeTypes.includes(file.type) || allowedExtensions.includes(fileExtension);
                
                if (isAllowedType) {
                    // Check file size
                    if (file.size > MAX_FILE_SIZE) {
                        const fileList = document.getElementById('fileList');
                        const errorDiv = document.createElement('div');
                        errorDiv.className = 'file-error';
                        errorDiv.textContent = `❌ ${file.name} é muito grande (máx: 5MB)`;
                        fileList.appendChild(errorDiv);
                        fileList.style.display = 'block';
                        
                        setTimeout(() => {
                            errorDiv.remove();
                            if (fileList.children.length === 0) {
                                fileList.style.display = 'none';
                            }
                        }, 3000);
                        continue;
                    }
                    
                    // Add to selected files
                    selectedFiles.push(file);
                    hasFiles = true;
                    
                    console.log('📎 File pasted:', file.name, file.type);
                } else {
                    const fileList = document.getElementById('fileList');
                    const errorDiv = document.createElement('div');
                    errorDiv.className = 'file-error';
                    errorDiv.textContent = `❌ ${file.name} não é um tipo de arquivo permitido`;
                    fileList.appendChild(errorDiv);
                    fileList.style.display = 'block';
                    
                    setTimeout(() => {
                        errorDiv.remove();
                        if (fileList.children.length === 0) {
                            fileList.style.display = 'none';
                        }
                    }, 3000);
                }
            }
        }
    }
    
    if (hasFiles) {
        renderFileList();
        
        // Show a notification to the user
        const fileList = document.getElementById('fileList');
        const notification = document.createElement('div');
        notification.className = 'file-success';
        notification.style.color = '#27ae60';
        notification.style.padding = '8px';
        notification.style.marginBottom = '8px';
        notification.textContent = `✓ ${selectedFiles.length === 1 ? 'Arquivo colado' : selectedFiles.length + ' arquivos colados'}`;
        fileList.insertBefore(notification, fileList.firstChild);
        
        setTimeout(() => {
            notification.remove();
        }, 2000);
    }
}

function updateSubmitButton() {
    const textarea = document.getElementById('commentText');	
    const submitBtn = document.getElementById('submitBtn');
    const hasText = textarea.value.trim().length > 0;
    // ✅ Comentário não precisa mais de rating
    submitBtn.disabled = !hasText;
}

function toggleCommentEditor() {
    const editor = document.getElementById('commentEditor');
    const button = document.querySelector('.btn-add-review');
    const sectionCard = document.querySelector('.right-column .section-card');
    
    // Check if editor is currently in reply mode (positioned inline elsewhere)
    const isInlineMode = replyingToCommentId !== null;
    // Also check if editor is not in its original container
    const isOutOfPlace = editor.parentNode !== sectionCard;
    
    // Cancel any inline edit in progress
    cancelInlineEdit();
    
    if (editor.classList.contains('show') && !isInlineMode && !isOutOfPlace) {
        // Close editor only if in original position and not in reply mode
        editor.classList.remove('show');
        button.classList.remove('active');
        resetCommentEditor();
    } else {
        // If in inline mode or out of place, reset and move back to top first
        if (isInlineMode || isOutOfPlace) {
            // First hide, reset, then show again
            editor.classList.remove('show');
            resetCommentEditor();
        }
        
        // Open editor at original position (top)
        editor.classList.add('show');
        button.classList.add('active');
        
        // Update subtitle based on current context
        updateEditorSubtitle();
        
        // Check if there's already text and enable submit button accordingly
        updateSubmitButton();
        
        // Scroll to editor at top and focus
        setTimeout(() => {
            editor.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            document.getElementById('commentText').focus();
        }, 100);
    }
}

function updateEditorSubtitle() {
    const subtitle = document.getElementById('editorSubtitle');
    
    // If replying, show reply message
    if (replyingToCommentId !== null) {
        subtitle.textContent = '💬 Respondendo ao comentário';
        return;
    }
    
    if (professorSelecionado !== null && PROFESSORES_DATA) {
        // Find professor name
        const prof = PROFESSORES_DATA.find(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorSelecionado);
        });
        
        const profNome = prof?.nome || prof?.name || 'este professor';
        subtitle.textContent = `Comentando sobre ${profNome}`;
    } else {
        subtitle.textContent = 'Comentando sobre a disciplina';
    }
}

// ✅ Removed selectRating, hoverRating, unhoverRating - no longer needed in comment modal

function resetCommentEditor() {
    // Clear textarea
    document.getElementById('commentText').value = '';
    
    // Clear files
    selectedFiles = [];
    document.getElementById('fileInput').value = '';
    renderFileList();
    
    // Clear reply mode
    replyingToCommentId = null;
    
    // Reset editor positioning and move back to original location
    const editor = document.getElementById('commentEditor');
    if (editor) {
        // Remove inline styles
        editor.style.position = '';
        editor.style.top = '';
        editor.style.left = '';
        editor.style.right = '';
        editor.style.marginLeft = '';
        editor.style.maxWidth = '';
        
        // Remove reply mode class
        editor.classList.remove('reply-mode');
        
        // Move editor back to its original container (inside .section-card, before .reviews-list)
        const sectionCard = document.querySelector('.right-column .section-card');
        const reviewsList = document.querySelector('.reviews-list');
        if (sectionCard && reviewsList && editor.parentNode !== sectionCard) {
            // Insert before reviews list (original position)
            sectionCard.insertBefore(editor, reviewsList);
        }
    }
    
    // Disable submit button
    document.getElementById('submitBtn').disabled = true;
}

// ============================================
// FILE UPLOAD FUNCTIONS
// ============================================

function handleFileSelect(event) {
    const files = Array.from(event.target.files);
    const fileList = document.getElementById('fileList');
    
    // Allowed file types for security
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
    const allowedMimeTypes = [
        'image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp',
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'text/plain'
    ];
    
    // Clear error messages
    fileList.querySelectorAll('.file-error').forEach(el => el.remove());
    
    files.forEach(file => {
        // Check file size
        if (file.size > MAX_FILE_SIZE) {
            const error = document.createElement('div');
            error.className = 'file-error';
            error.textContent = `❌ Arquivo "${file.name}" excede o tamanho máximo de 5MB`;
            fileList.appendChild(error);
            return;
        }
        
        // Check file extension
        const extension = '.' + file.name.split('.').pop().toLowerCase();
        if (!allowedExtensions.includes(extension)) {
            const error = document.createElement('div');
            error.className = 'file-error';
            error.textContent = `❌ Tipo de arquivo não permitido: "${file.name}". Apenas imagens (JPG, PNG, GIF, WebP) e documentos (PDF, DOC, DOCX, XLS, XLSX, TXT) são permitidos.`;
            fileList.appendChild(error);
            return;
        }
        
        // Check MIME type
        if (!allowedMimeTypes.includes(file.type)) {
            const error = document.createElement('div');
            error.className = 'file-error';
            error.textContent = `❌ Tipo de arquivo não permitido: "${file.name}". Apenas imagens e documentos seguros são permitidos.`;
            fileList.appendChild(error);
            return;
        }
        
        // Check if file already exists
        const exists = selectedFiles.some(f => f.name === file.name && f.size === file.size);
        if (!exists) {
            selectedFiles.push(file);
        }
    });
    
    // Clear input to allow selecting the same file again
    event.target.value = '';
    
    renderFileList();
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    renderFileList();
}

function renderFileList() {
    const fileList = document.getElementById('fileList');
    const errors = fileList.querySelectorAll('.file-error');
    
    if (selectedFiles.length === 0 && errors.length === 0) {
        fileList.innerHTML = '';
        return;
    }
    
    const filesHTML = selectedFiles.map((file, index) => `
        <div class="file-item">
            <div class="file-item-info">
                <span class="file-item-icon">${getFileIcon(file.name)}</span>
                <span class="file-item-name">${file.name}</span>
                <span class="file-item-size">${formatFileSize(file.size)}</span>
            </div>
            <button type="button" class="file-item-remove" onclick="removeFile(${index})" title="Remover arquivo">×</button>
        </div>
    `).join('');
    
    // Preserve error messages
    const errorsHTML = Array.from(errors).map(el => el.outerHTML).join('');
    
    fileList.innerHTML = filesHTML + errorsHTML;
}

function getFileIcon(filename) {
    const ext = filename.split('.').pop().toLowerCase();
    const icons = {
        'pdf': '📄',
        'doc': '📝',
        'docx': '📝',
        'jpg': '🖼️',
        'jpeg': '🖼️',
        'png': '🖼️',
        'gif': '🖼️'
    };
    return icons[ext] || '📎';
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// ============================================
// SUBMIT COMMENT
// ============================================

async function submitComment(event) {
    event.preventDefault();
    
    const texto = document.getElementById('commentText').value.trim();
    const submitButton = event.target.querySelector('button[type="submit"]');
    const commentForm = document.getElementById('commentForm');
    
    if (!texto) {
        showToast('Por favor, escreva um comentário.', 'warning');
        return;
    }
    
    // Disable form and show loading
    if (submitButton) {
        submitButton.disabled = true;
        submitButton.classList.add('btn-loading');
        const originalText = submitButton.textContent;
        submitButton.dataset.originalText = originalText;
        submitButton.textContent = 'Enviando...';
    }
    
    if (commentForm) {
        commentForm.classList.add('form-disabled');
    }
    
    // Check if we're replying or creating a new comment
    if (replyingToCommentId !== null) {
        // REPLY MODE
        console.log('Submitting reply to comment:', {
            parentId: replyingToCommentId,
            texto,
            files: selectedFiles.map(f => ({ name: f.name, size: f.size, type: f.type }))
        });
		  
        // Prepare FormData for reply
        const formData = new FormData();
        formData.append('texto', texto);
        formData.append('comentarioPaiId', replyingToCommentId);
        
        selectedFiles.forEach((file, index) => {
            formData.append(`files`, file);
        });
        
		  console.log('FormData for reply:', formData);
        try {
            const response = await fetch('/api/comentario/responder', {
                method: 'POST',
                body: formData
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                showToast(parseErrorMessage(errorText) || 'Erro ao enviar resposta', 'error');
                return;
            }
            
            const result = await response.json();
            console.log('Resposta enviada com sucesso:', result);
            
            // Reload page to show new reply
            window.location.reload();
            
        } catch (error) {
            console.error('Erro ao enviar resposta:', error);
            showToast(parseErrorMessage(error.message) || 'Erro ao enviar resposta', 'error');
            
            // Re-enable form on error
            if (submitButton) {
                submitButton.disabled = false;
                submitButton.classList.remove('btn-loading');
                submitButton.textContent = submitButton.dataset.originalText || 'Enviar';
            }
            if (commentForm) {
                commentForm.classList.remove('form-disabled');
            }
            return;
        }
    } else {
        // CREATE MODE (new comment)
        console.log('Submitting comment:', {
            texto,
            professorId: professorSelecionado,
            disciplinaId: typeof CLASS_ID !== 'undefined' ? CLASS_ID : null,
            files: selectedFiles.map(f => ({ name: f.name, size: f.size, type: f.type }))
        });
        
        // Prepare FormData for file upload
        const formData = new FormData();
        formData.append('texto', texto);
        formData.append('disciplinaId', typeof CLASS_ID !== 'undefined' ? CLASS_ID : '');
        if (professorSelecionado) {
            formData.append('professorId', professorSelecionado);
        } else {
            formData.append('professorId', "");
        }
        selectedFiles.forEach((file, index) => {
            formData.append(`files`, file);
        });
        
        try {
            const response = await fetch('/api/comentario/comentar', {
                method: 'POST',
                body: formData
            });
            if (!response.ok) {
                const errorText = await response.text();
                showToast(parseErrorMessage(errorText) || 'Erro ao enviar comentário', 'error');
                return;
            }
            const result = await response.json();
            console.log('Comentário enviado com sucesso:', result);
            
            // Reload page to show new comment
            window.location.reload();
            
        } catch (error) {
            console.error('Erro ao enviar comentário:', error);
            showToast(parseErrorMessage(error.message) || 'Erro ao enviar comentário', 'error');
            
            // Re-enable form on error
            if (submitButton) {
                submitButton.disabled = false;
                submitButton.classList.remove('btn-loading');
                submitButton.textContent = submitButton.dataset.originalText || 'Enviar';
            }
            if (commentForm) {
                commentForm.classList.remove('form-disabled');
            }
            return;
        }
    }
}

// ============================================
// VALIDATION & INTERACTION FUNCTIONS
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

/**
 * Votar em um comentário (upvote ou downvote)
 */
async function voteComment(comentarioId, isUpVote) {
    // Find the comment card and buttons
    const commentCard = document.querySelector(`[data-comment-id="${comentarioId}"]`);
    if (!commentCard) return;
    
    const upvoteBtn = commentCard.querySelector('.upvote-btn');
    const downvoteBtn = commentCard.querySelector('.downvote-btn');
    const comentario = allComments.find(c => c.id === comentarioId);
    
    if (!comentario) return;
    
    // Store original state for rollback
    const originalUpVotes = comentario.upVotes || 0;
    const originalDownVotes = comentario.downVotes || 0;
    const originalHasVoted = comentario.hasVoted || 0;
    
    // OPTIMISTIC UI UPDATE - Immediate visual feedback
    const currentVote = comentario.hasVoted;
    
    // Calculate new state optimistically
    if (isUpVote) {
        if (currentVote === 1) {
            // Remove upvote
            comentario.upVotes = originalUpVotes - 1;
            comentario.hasVoted = 0;
            upvoteBtn.classList.remove('voted');
        } else if (currentVote === -1) {
            // Switch from downvote to upvote
            comentario.upVotes = originalUpVotes + 1;
            comentario.downVotes = originalDownVotes - 1;
            comentario.hasVoted = 1;
            upvoteBtn.classList.add('voted');
            downvoteBtn.classList.remove('voted');
        } else {
            // Add upvote
            comentario.upVotes = originalUpVotes + 1;
            comentario.hasVoted = 1;
            upvoteBtn.classList.add('voted');
        }
    } else {
        if (currentVote === -1) {
            // Remove downvote
            comentario.downVotes = originalDownVotes - 1;
            comentario.hasVoted = 0;
            downvoteBtn.classList.remove('voted');
        } else if (currentVote === 1) {
            // Switch from upvote to downvote
            comentario.downVotes = originalDownVotes + 1;
            comentario.upVotes = originalUpVotes - 1;
            comentario.hasVoted = -1;
            downvoteBtn.classList.add('voted');
            upvoteBtn.classList.remove('voted');
        } else {
            // Add downvote
            comentario.downVotes = originalDownVotes + 1;
            comentario.hasVoted = -1;
            downvoteBtn.classList.add('voted');
        }
    }
    
    // Update vote score in UI (Reddit style: upvotes - downvotes)
    const voteScore = commentCard.querySelector('.vote-score');
    if (voteScore) {
        const score = (comentario.upVotes || 0) - (comentario.downVotes || 0);
        voteScore.textContent = score;
        voteScore.classList.remove('positive', 'negative');
        if (score > 0) voteScore.classList.add('positive');
        else if (score < 0) voteScore.classList.add('negative');
    }
    
    // Add pending state
    commentCard.classList.add('optimistic-pending');
    upvoteBtn.disabled = true;
    downvoteBtn.disabled = true;
    
    try {
        const formData = new FormData();
        formData.append('isUpVote', isUpVote);
        
        console.log(`Votando no comentário ${comentarioId} - Upvote: ${isUpVote}`);

        const response = await fetch(`/api/comentario/votar/${comentarioId}`, {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            // ROLLBACK on error
            comentario.upVotes = originalUpVotes;
            comentario.downVotes = originalDownVotes;
            comentario.hasVoted = originalHasVoted;
            
            // Update vote score display
            const voteScoreRollback = commentCard.querySelector('.vote-score');
            if (voteScoreRollback) {
                const scoreRollback = originalUpVotes - originalDownVotes;
                voteScoreRollback.textContent = scoreRollback;
                voteScoreRollback.classList.remove('positive', 'negative');
                if (scoreRollback > 0) voteScoreRollback.classList.add('positive');
                else if (scoreRollback < 0) voteScoreRollback.classList.add('negative');
            }
            
            if (originalHasVoted === 1) upvoteBtn.classList.add('voted');
            else upvoteBtn.classList.remove('voted');
            if (originalHasVoted === -1) downvoteBtn.classList.add('voted');
            else downvoteBtn.classList.remove('voted');
            
            commentCard.classList.add('optimistic-error');
            setTimeout(() => commentCard.classList.remove('optimistic-error'), 500);
            showToast(parseErrorMessage(errorText) || 'Erro ao votar', 'error');
            return;
        }
        
        const result = await response.json();
        console.log('Voto registrado:', result);
        
        // Update with actual server values
        comentario.upVotes = result.upVotes || 0;
        comentario.downVotes = result.downVotes || 0;
        if (result.userVote !== undefined) {
            comentario.hasVoted = result.userVote;
        }
        
        // Update vote score display with server values
        const voteScoreServer = commentCard.querySelector('.vote-score');
        if (voteScoreServer) {
            const scoreServer = comentario.upVotes - comentario.downVotes;
            voteScoreServer.textContent = scoreServer;
            voteScoreServer.classList.remove('positive', 'negative');
            if (scoreServer > 0) voteScoreServer.classList.add('positive');
            else if (scoreServer < 0) voteScoreServer.classList.add('negative');
        }
        
        // Success animation
        commentCard.classList.remove('optimistic-pending');
        commentCard.classList.add('optimistic-success');
        setTimeout(() => commentCard.classList.remove('optimistic-success'), 500);
        
    } catch (error) {
        console.error('Erro ao votar:', error);
        // ROLLBACK on error
        comentario.upVotes = originalUpVotes;
        comentario.downVotes = originalDownVotes;
        comentario.hasVoted = originalHasVoted;
        
        // Update vote score display
        const voteScoreCatch = commentCard.querySelector('.vote-score');
        if (voteScoreCatch) {
            const scoreCatch = originalUpVotes - originalDownVotes;
            voteScoreCatch.textContent = scoreCatch;
            voteScoreCatch.classList.remove('positive', 'negative');
            if (scoreCatch > 0) voteScoreCatch.classList.add('positive');
            else if (scoreCatch < 0) voteScoreCatch.classList.add('negative');
        }
        
        if (originalHasVoted === 1) upvoteBtn.classList.add('voted');
        else upvoteBtn.classList.remove('voted');
        if (originalHasVoted === -1) downvoteBtn.classList.add('voted');
        else downvoteBtn.classList.remove('voted');
        
        commentCard.classList.add('optimistic-error');
        setTimeout(() => commentCard.classList.remove('optimistic-error'), 500);
        showToast(parseErrorMessage(error.message) || 'Erro ao votar', 'error');
    } finally {
        commentCard.classList.remove('optimistic-pending');
        upvoteBtn.disabled = false;
        downvoteBtn.disabled = false;
    }
}

/**
 * Responder a um comentário
 */
function replyToComment(comentarioId) {
    console.log('Reply to comment:', comentarioId);
    
    // Store which comment we're replying to
    replyingToCommentId = comentarioId;
    
    // Find the comment card
    const commentCard = document.querySelector(`[data-comment-id="${comentarioId}"]`);
    if (!commentCard) {
        showToast('Comentário não encontrado.', 'error');
        return;
    }
    
    // Get the comment editor
    const editor = document.getElementById('commentEditor');
    const button = document.querySelector('.btn-add-review');
    
    // Reset editor first (but keep replyingToCommentId)
    const savedReplyId = replyingToCommentId;
    resetCommentEditor();
    replyingToCommentId = savedReplyId;
    
    // Update subtitle to show we're replying
    const subtitle = document.getElementById('editorSubtitle');
    if (subtitle) {
        subtitle.textContent = '💬 Respondendo ao comentário';
    }
    
    // Show the editor if hidden
    if (!editor.classList.contains('show')) {
        editor.classList.add('show');
        if (button) button.classList.add('active');
    }
    
    // Remove any previous inline positioning from absolute mode
    editor.style.position = '';
    editor.style.top = '';
    editor.style.left = '';
    editor.style.right = '';
    editor.style.maxWidth = '';
    
    // Add reply mode class and indent styling
    editor.classList.add('reply-mode');
    editor.style.marginLeft = '10px'; // Small indent to the right
    
    // Insert the editor right after the comment card in the DOM
    // This makes it part of the document flow
    if (commentCard.nextSibling) {
        commentCard.parentNode.insertBefore(editor, commentCard.nextSibling);
    } else {
        commentCard.parentNode.appendChild(editor);
    }
    
    // Scroll to the editor smoothly
    setTimeout(() => {
        editor.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        document.getElementById('commentText').focus();
    }, 100);
}

/**
 * Editar um comentário existente - Edição Inline
 */
function editComment(comentarioId) {
    // Find the comment data
    const comentario = allComments.find(c => c.id === comentarioId);
    
    if (!comentario) {
        showToast('Comentário não encontrado.', 'error');
        return;
    }
    
    // Find the comment card element
    const commentCard = document.querySelector(`[data-comment-id="${comentarioId}"]`);
    if (!commentCard) {
        showToast('Elemento do comentário não encontrado.', 'error');
        return;
    }
    
    // Check if already editing another comment
    const existingEditContainer = document.querySelector('.inline-edit-container');
    if (existingEditContainer) {
        cancelInlineEdit();
    }
    
    // Store editing state
    editingCommentId = comentarioId;
    inlineEditFiles = [];
    inlineEditExistingFiles = comentario.arquivos ? [...comentario.arquivos] : [];
    
    // Add editing class to card
    commentCard.classList.add('editing');
    
    // Hide attachments during editing
    const attachments = commentCard.querySelector('.comment-attachments');
    if (attachments) {
        attachments.style.display = 'none';
    }
    
    // Get the review-content element
    const reviewContent = commentCard.querySelector('.review-content');
    
    // Create inline edit container with file upload
    const editContainer = document.createElement('div');
    editContainer.className = 'inline-edit-container';
    editContainer.innerHTML = `
        <textarea class="inline-edit-textarea" id="inlineEditText">${escapeHtml(comentario.texto)}</textarea>
        
        <!-- File Upload Section -->
        <div class="inline-edit-files">
            <div class="inline-edit-file-input-wrapper">
                <input type="file" id="inlineEditFileInput" class="file-input" multiple 
                    accept=".pdf,.doc,.docx,.xls,.xlsx,.txt,.jpg,.jpeg,.png,.gif,.webp,image/*,application/pdf">
                <label for="inlineEditFileInput" class="file-upload-label inline-edit-file-label">
                    <span class="file-icon">📎</span>
                    <span class="file-text">Anexar arquivos</span>
                </label>
            </div>
            <div id="inlineEditFileList" class="inline-edit-file-list"></div>
        </div>
        
        <div class="inline-edit-actions">
            <button type="button" class="btn-cancel" onclick="cancelInlineEdit()">Cancelar</button>
            <button type="button" class="btn-save" id="inlineEditSaveBtn" onclick="saveInlineEdit(${comentarioId})">Salvar</button>
        </div>
    `;
    
    // Insert after review-content
    reviewContent.parentNode.insertBefore(editContainer, reviewContent.nextSibling);
    
    // Render existing files
    renderInlineEditFileList();
    
    // Add file input listener
    const fileInput = document.getElementById('inlineEditFileInput');
    fileInput.addEventListener('change', handleInlineEditFileSelect);
    
    // Focus on textarea and move cursor to end
    const textarea = editContainer.querySelector('.inline-edit-textarea');
    textarea.focus();
    textarea.setSelectionRange(textarea.value.length, textarea.value.length);
    
    // Add input listener to enable/disable save button
    textarea.addEventListener('input', updateInlineEditSaveButton);
    
    // Scroll to make edit visible
    commentCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

/**
 * Handle file selection for inline edit
 */
function handleInlineEditFileSelect(event) {
    const files = Array.from(event.target.files);
    
    const allowedExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.txt'];
    const allowedMimeTypes = [
        'image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp',
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'text/plain'
    ];
    
    files.forEach(file => {
        // Check file size
        if (file.size > MAX_FILE_SIZE) {
            showToast(`Arquivo "${file.name}" excede o tamanho máximo de 5MB`, 'error');
            return;
        }
        
        // Check file extension
        const extension = '.' + file.name.split('.').pop().toLowerCase();
        if (!allowedExtensions.includes(extension)) {
            showToast(`Tipo de arquivo não permitido: "${file.name}"`, 'error');
            return;
        }
        
        // Check MIME type
        if (!allowedMimeTypes.includes(file.type)) {
            showToast(`Tipo de arquivo não permitido: "${file.name}"`, 'error');
            return;
        }
        
        // Check if file already exists in new files
        const existsInNew = inlineEditFiles.some(f => f.name === file.name && f.size === file.size);
        // Check if file already exists in existing files
        const existsInExisting = inlineEditExistingFiles.some(f => f.nomeOriginal === file.name);
        
        if (!existsInNew && !existsInExisting) {
            inlineEditFiles.push(file);
        }
    });
    
    // Clear input to allow selecting the same file again
    event.target.value = '';
    
    renderInlineEditFileList();
}

/**
 * Render file list for inline edit
 */
function renderInlineEditFileList() {
    const fileList = document.getElementById('inlineEditFileList');
    if (!fileList) return;
    
    let html = '';
    
    // Render existing files (from server)
    inlineEditExistingFiles.forEach((arquivo, index) => {
        const icon = getFileIcon(arquivo.nomeOriginal);
        html += `
            <div class="inline-edit-file-item existing-file">
                <div class="file-item-info">
                    <span class="file-item-icon">${icon}</span>
                    <span class="file-item-name">${escapeHtml(arquivo.nomeOriginal)}</span>
                    <span class="file-item-size">${formatFileSize(arquivo.tamanho)}</span>
                </div>
                <button type="button" class="file-item-remove" onclick="removeInlineEditExistingFile(${index})" title="Remover arquivo">×</button>
            </div>
        `;
    });
    
    // Render new files (to be uploaded)
    inlineEditFiles.forEach((file, index) => {
        const icon = getFileIcon(file.name);
        html += `
            <div class="inline-edit-file-item new-file">
                <div class="file-item-info">
                    <span class="file-item-icon">${icon}</span>
                    <span class="file-item-name">${escapeHtml(file.name)}</span>
                    <span class="file-item-size">${formatFileSize(file.size)}</span>
                    <span class="file-item-badge">Novo</span>
                </div>
                <button type="button" class="file-item-remove" onclick="removeInlineEditNewFile(${index})" title="Remover arquivo">×</button>
            </div>
        `;
    });
    
    fileList.innerHTML = html;
}

/**
 * Remove existing file from inline edit
 */
function removeInlineEditExistingFile(index) {
    inlineEditExistingFiles.splice(index, 1);
    renderInlineEditFileList();
}

/**
 * Remove new file from inline edit
 */
function removeInlineEditNewFile(index) {
    inlineEditFiles.splice(index, 1);
    renderInlineEditFileList();
}

/**
 * Update save button state for inline edit
 */
function updateInlineEditSaveButton() {
    const textarea = document.getElementById('inlineEditText');
    const saveBtn = document.getElementById('inlineEditSaveBtn');
    if (textarea && saveBtn) {
        const hasText = textarea.value.trim().length > 0;
        saveBtn.disabled = !hasText;
    }
}

/**
 * Cancelar edição inline
 */
function cancelInlineEdit() {
    const editContainer = document.querySelector('.inline-edit-container');
    if (editContainer) {
        const commentCard = editContainer.closest('.review-card');
        if (commentCard) {
            commentCard.classList.remove('editing');
            
            // Restore attachments visibility
            const attachments = commentCard.querySelector('.comment-attachments');
            if (attachments) {
                attachments.style.display = '';
            }
        }
        editContainer.remove();
    }
    
    // Clear editing state
    editingCommentId = null;
    inlineEditFiles = [];
    inlineEditExistingFiles = [];
}

/**
 * Salvar edição inline
 */
async function saveInlineEdit(comentarioId) {
    const textarea = document.getElementById('inlineEditText');
    const saveBtn = document.getElementById('inlineEditSaveBtn');
    const novoTexto = textarea.value.trim();
    
    if (!novoTexto) {
        showToast('O comentário não pode estar vazio.', 'error');
        return;
    }
    
    // Disable button and show loading
    saveBtn.disabled = true;
    saveBtn.textContent = 'Salvando...';
    
    try {
        const formData = new FormData();
        formData.append('novoTexto', novoTexto);
        
        // Add IDs of existing files to keep
        inlineEditExistingFiles.forEach(arquivo => {
            formData.append('existingFileIds', arquivo.id);
        });
        
        // Add new files to upload
        inlineEditFiles.forEach(file => {
            formData.append('files', file);
        });
        
        const response = await fetch(`/api/comentario/editar/${comentarioId}`, {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            showToast(parseErrorMessage(errorText) || 'Erro ao editar comentário', 'error');
            saveBtn.disabled = false;
            saveBtn.textContent = 'Salvar';
            return;
        }
        
        const result = await response.json();
        console.log('Comentário editado com sucesso:', result);
        
        // Reload page to show updated comment with new files
        // This is simpler than trying to update all the file attachments in place
        window.location.reload();
        
    } catch (error) {
        console.error('Erro ao editar comentário:', error);
        showToast(parseErrorMessage(error.message) || 'Erro ao editar comentário', 'error');
        saveBtn.disabled = false;
        saveBtn.textContent = 'Salvar';
    }
}


function markAsDeleted(comentario){
	comentario.deleted = true;
	if(comentario.filhos && comentario.filhos.length > 0){
		comentario.filhos.forEach(filho => markAsDeleted(filho));
	}
}

/**
 * Deletar um comentário
 */
async function deleteComment(comentarioId) {
    // Confirmação antes de deletar
    if (!confirm('Tem certeza que deseja deletar este comentário? Esta ação não pode ser desfeita.')) {
        return;
    }
    
    // Find the comment card and delete button
    const commentCard = document.querySelector(`[data-comment-id="${comentarioId}"]`);
    const deleteButton = commentCard?.querySelector('.delete-btn');
    
    // Add loading state to button
    if (deleteButton) {
        deleteButton.disabled = true;
        deleteButton.classList.add('btn-loading');
        const originalText = deleteButton.innerHTML;
        deleteButton.dataset.originalHtml = originalText;
        deleteButton.innerHTML = '<span class="vote-icon">⏳</span><span>Deletando...</span>';
    }
    
    // Add loading overlay to card
    if (commentCard) {
        commentCard.classList.add('optimistic-pending');
    }
    
    try {
        console.log(`Deletando comentário ${comentarioId}...`);
        
        const response = await fetch(`/api/comentario/delete/${comentarioId}`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            
            // Restore button state on error
            if (deleteButton) {
                deleteButton.disabled = false;
                deleteButton.classList.remove('btn-loading');
                deleteButton.innerHTML = deleteButton.dataset.originalHtml || originalText;
            }
            
            if (commentCard) {
                commentCard.classList.remove('optimistic-pending');
                commentCard.classList.add('optimistic-error');
                setTimeout(() => commentCard.classList.remove('optimistic-error'), 500);
            }
            
            showToast(parseErrorMessage(errorText) || 'Erro ao deletar comentário', 'error');
            return;
        }
        
        console.log('Comentário deletado com sucesso');
        
        // Success animation before removal
        if (commentCard) {
            commentCard.classList.remove('optimistic-pending');
            commentCard.classList.add('optimistic-success');
        }
        
        // Wait for animation before removing from DOM
        setTimeout(() => {
            // ✅ Remover comentário do array global
            const indexGlobal = allComments.findIndex(c => c.id === comentarioId);
				
            if (indexGlobal !== -1) {
					markAsDeleted(allComments[indexGlobal]);
            }
            
            // ✅ Atualizar visualização - comentários agora são apenas de professores
            if (professorSelecionado !== null) {
                const comentariosProfessor = allComments.filter(c => 
                    String(c.professorId) === String(professorSelecionado)
                );
                
                // Encontrar nome do professor
                const profIndex = PROFESSORES_DATA.findIndex(p => {
                    const profId = p.id || p.professorId || p.ID;
                    return String(profId) === String(professorSelecionado);
                });
                
                const profNome = profIndex !== -1 
                    ? (PROFESSORES_DATA[profIndex]?.nome || PROFESSORES_DATA[profIndex]?.name || 'Professor')
                    : 'Professor';
                
                mostrarComentarios(comentariosProfessor, profNome);
            }
        }, 300); // Small delay for animation
        
        // ✅ Feedback visual
        showToast('Comentário deletado com sucesso!', 'success');
        
    } catch (error) {
        console.error('Erro ao deletar comentário:', error);
        showToast(parseErrorMessage(error.message) || 'Erro ao deletar comentário', 'error');
    }
}

/**
 * Escapar HTML para prevenir XSS em strings interpoladas
 */
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;',
        '`': '&#x60;',
        '\\': '&#x5C;'
    };
    return text.replace(/[&<>"'`\\]/g, char => map[char]);
}
