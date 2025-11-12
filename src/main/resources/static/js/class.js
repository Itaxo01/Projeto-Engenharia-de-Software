/**
 * Página da Disciplina - JavaScript
 */

console.log('✅ class.js carregado');

let professorSelecionado = null;
let avaliacoesDisciplinaGlobal = [];
let selectedRating = 0;
let selectedFiles = [];
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
let currentUserEmail = null; // Email do usuário logado

document.addEventListener('DOMContentLoaded', function() {
    if (typeof AVALIACOES_DATA === 'undefined') return;
    
    // Obter email do usuário logado
    currentUserEmail = typeof USER_EMAIL !== 'undefined' ? USER_EMAIL : null;
    
    console.log('📊 Dados carregados:', {
        avaliacoes: AVALIACOES_DATA.length,
        professores: PROFESSORES_DATA?.length || 0,
        usuarioLogado: currentUserEmail
    });

	 AVALIACOES_DATA.forEach(a => {
		console.log('Avaliação:', a);
	 });
    
    // Separar avaliações
    const avaliacoesDisciplina = AVALIACOES_DATA.filter(a => !a.professorId);
    const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
    
    // Armazenar avaliações da disciplina globalmente
    avaliacoesDisciplinaGlobal = avaliacoesDisciplina;
    
    // Atualizar disciplina
    const statsDisciplina = calcularStats(avaliacoesDisciplina);
    atualizarDisciplina(statsDisciplina, avaliacoesDisciplina);
    
    // Atualizar professores e adicionar event listeners
    if (typeof PROFESSORES_DATA !== 'undefined') {
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
                    // Toggle: se já está selecionado, desseleciona (comparar como string)
                    if (String(professorSelecionado) === String(profId)) {
                        console.log('Desselecionando professor:', profId);
                        deselecionarProfessor();
                    } else {
                        console.log('Alternando para professor:', profId, profNome);
                        selecionarProfessor(profId, profNome, avaliacoesProf);
                    }
                });
            }
        });
    }
    
    // Event listener no header da disciplina para voltar às avaliações da disciplina
    const disciplineHeader = document.querySelector('.discipline-header');
    if (disciplineHeader) {
        disciplineHeader.style.cursor = 'pointer';
        disciplineHeader.addEventListener('click', () => {
            deselecionarProfessor();
        });
    }
    
    // Setup comment editor
    setupCommentEditor();
});

// Calcular média e total
function calcularStats(avaliacoes) {
    const comNota = avaliacoes.filter(a => a.nota > 0);
    if (comNota.length === 0) return { media: 0, total: 0 };
    
    const soma = comNota.reduce((acc, a) => acc + a.nota, 0);
    return { media: soma / comNota.length, total: comNota.length };
}

// Atualizar header da disciplina
function atualizarDisciplina(stats, avaliacoes) {
    document.querySelector('.discipline-rating .rating-score').textContent = 
        stats.total > 0 ? stats.media.toFixed(1) : 'N/A';
    
    document.querySelector('.discipline-rating .rating-count').textContent = 
        `${stats.total} ${stats.total === 1 ? 'avaliação' : 'avaliações'}`;
    
    preencherEstrelas(document.querySelector('.discipline-rating .rating-stars'), stats.media);
    
    // Mostrar comentários (filtra avaliações que têm comentário)
    const comComentario = avaliacoes.filter(a => a.comentario && a.comentario.texto);
    mostrarComentarios(comComentario, null);
}

// Selecionar professor
function selecionarProfessor(professorId, professorNome, avaliacoes) {
    console.log('Selecionando professor:', professorId, 'Atual:', professorSelecionado);
    
    // Atualizar professor selecionado
    professorSelecionado = professorId;
    
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
    
    // Atualizar título da seção de avaliações
    const sectionTitle = document.querySelector('.section-header h2');
    if (sectionTitle) {
        sectionTitle.textContent = `Avaliações - ${professorNome}`;
    }
    
    // Mostrar comentários do professor
    const comComentario = avaliacoes.filter(a => a.comentario && a.comentario.texto);
    mostrarComentarios(comComentario, professorNome);
}

// Desselecionar professor
function deselecionarProfessor() {
    console.log('Desselecionando professor');
    professorSelecionado = null;
    
    // Remover todas as seleções
    document.querySelectorAll('.professor-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // Restaurar título original
    const sectionTitle = document.querySelector('.section-header h2');
    if (sectionTitle) {
        sectionTitle.textContent = 'Avaliações';
    }
    
    // Mostrar comentários da disciplina novamente
    const comComentario = avaliacoesDisciplinaGlobal.filter(a => a.comentario && a.comentario.texto);
    mostrarComentarios(comComentario, null);
}

// Atualizar professor específico
function atualizarProfessor(index, stats) {
    const professorItem = document.querySelectorAll('.professor-item')[index];
    if (!professorItem) return;
    
    professorItem.querySelector('.rating-value').textContent = 
        stats.total > 0 ? stats.media.toFixed(1) : 'N/A';
    
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
function mostrarComentarios(avaliacoes, professorNome) {
    const lista = document.querySelector('.reviews-list');
    
    if (avaliacoes.length === 0) {
        const entidade = professorNome || 'esta disciplina';
        lista.innerHTML = `<div class="no-reviews"><p>Ainda não há avaliações para ${entidade}.</p><p>Seja o primeiro a avaliar!</p></div>`;
        return;
    }
    
    lista.innerHTML = avaliacoes.map(a => {
        const comentario = a.comentario;
        const isOwner = comentario.isOwner || false;
        const hasVoted = comentario.hasVoted; // 1 (upvote), -1 (downvote), 0 (no vote)
        
        // Classes para destacar botões de voto
        const upvoteClass = hasVoted === 1 ? 'voted' : '';
        const downvoteClass = hasVoted === -1 ? 'voted' : '';
        
        return `
        <div class="review-card">
            <div class="review-header">
                <div class="reviewer-info">
                    <div class="reviewer-avatar">${(comentario.nomeUsuario || 'A')[0].toUpperCase()}</div>
                    <span class="reviewer-name">${comentario.nomeUsuario || 'Usuário Anônimo'}</span>
                    <span class="reviewer-date">${formatarData(comentario.createdAt)}</span>
                </div>
                <div class="review-rating" data-nota="${a.nota}">
                    <div class="rating-stars">
                        ${'<span class="star">★</span>'.repeat(5)}
                    </div>
                </div>
            </div>
            <div class="review-content">${comentario.texto}</div>
            
            <!-- Botões de Ação -->
            <div class="review-actions">
                <button class="review-action-btn upvote-btn ${upvoteClass}" onclick="voteComment(${comentario.id}, true)">
                    <span class="vote-icon">👍</span>
                    <span class="vote-count">${comentario.upVotes || 0}</span>
                </button>
                <button class="review-action-btn downvote-btn ${downvoteClass}" onclick="voteComment(${comentario.id}, false)">
                    <span class="vote-icon">👎</span>
                    <span class="vote-count">${comentario.downVotes || 0}</span>
                </button>
                <button class="review-action-btn reply-btn" onclick="replyToComment(${comentario.id})">
                    <span class="vote-icon">💬</span>
                    <span>Responder</span>
                </button>
                ${isOwner ? `
                <button class="review-action-btn edit-btn" onclick="editComment(${comentario.id}, '${escapeHtml(comentario.texto)}', ${a.nota})">
                    <span class="vote-icon">✏️</span>
                    <span>Editar</span>
                </button>
                ` : ''}
            </div>
        </div>
        `;
    }).join('');
    
    // Preencher estrelas dos comentários
    lista.querySelectorAll('.review-rating').forEach(el => {
        preencherEstrelas(el.querySelector('.rating-stars'), parseFloat(el.dataset.nota));
    });
}

// Formatar data
function formatarData(data) {
    if (!data) return 'Data desconhecida';
    const d = new Date(data);
    const meses = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
    return `${meses[d.getMonth()]} ${d.getFullYear()}`;
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
    }
}

function updateSubmitButton() {
    const textarea = document.getElementById('commentText');	
    const submitBtn = document.getElementById('submitBtn');
    const hasText = textarea.value.trim().length > 0;
    submitBtn.disabled = !(hasText && selectedRating > 0);
}

function toggleCommentEditor() {
    const editor = document.getElementById('commentEditor');
    const button = document.querySelector('.btn-add-review');
    
    if (editor.classList.contains('show')) {
        // Close editor
        editor.classList.remove('show');
        button.classList.remove('active');
        resetCommentEditor();
    } else {
        // ✅ Verificar se usuário já avaliou este contexto
        if (jaAvaliouContextoAtual()) {
            alert('Você já avaliou este contexto. Cada usuário pode fazer apenas uma avaliação por professor/disciplina.');
            return;
        }
        
        // Open editor
        editor.classList.add('show');
        button.classList.add('active');
        
        // Update subtitle based on current context
        updateEditorSubtitle();
        
        // Focus on textarea
        setTimeout(() => {
            document.getElementById('commentText').focus();
        }, 100);
    }
}

function updateEditorSubtitle() {
    const subtitle = document.getElementById('editorSubtitle');
    
    if (professorSelecionado !== null && PROFESSORES_DATA) {
        // Find professor name
        const prof = PROFESSORES_DATA.find(p => {
            const profId = p.id || p.professorId || p.ID;
            return String(profId) === String(professorSelecionado);
        });
        
        const profNome = prof?.nome || prof?.name || 'este professor';
        subtitle.textContent = `Avaliando ${profNome}`;
    } else {
        subtitle.textContent = 'Avaliando a disciplina';
    }
}

function selectRating(rating) {
    selectedRating = rating;
    
    const stars = document.querySelectorAll('#ratingSelectorStars .star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('selected');
        } else {
            star.classList.remove('selected');
        }
    });
    
    updateSubmitButton();
}

function hoverRating(rating) {
    const stars = document.querySelectorAll('#ratingSelectorStars .star');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('hover');
        } else {
            star.classList.remove('hover');
        }
    });
}

function unhoverRating() {
    const stars = document.querySelectorAll('#ratingSelectorStars .star');
    stars.forEach(star => {
        star.classList.remove('hover');
    });
}

function resetCommentEditor() {
    // Clear textarea
    document.getElementById('commentText').value = '';
    
    // Reset rating
    selectedRating = 0;
    const stars = document.querySelectorAll('#ratingSelectorStars .star');
    stars.forEach(star => {
        star.classList.remove('selected', 'hover');
    });
    
    // Clear files
    selectedFiles = [];
    document.getElementById('fileInput').value = '';
    renderFileList();
    
    // Disable submit button
    document.getElementById('submitBtn').disabled = true;
}

// ============================================
// FILE UPLOAD FUNCTIONS
// ============================================

function handleFileSelect(event) {
    const files = Array.from(event.target.files);
    const fileList = document.getElementById('fileList');
    
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
    
    if (!texto || selectedRating === 0) {
        alert('Por favor, preencha todos os campos obrigatórios.');
        return;
    }
    
    console.log('Submitting comment:', {
        texto,
        nota: selectedRating,
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
		formData.append('nota', selectedRating);
    selectedFiles.forEach((file, index) => {
        formData.append(`arquivo${index}`, file);
    });
    
    try {
		const response = await fetch('/api/comentario/criar', {
				method: 'POST',
				body: formData
		});
		if (!response.ok) {
				const errorText = await response.text();
				alert('Erro ao enviar comentário: ' + errorText);
				return;
		}
		const result = await response.json();
		console.log('Comentário enviado com sucesso:', result);
        
        // ✅ Adicionar nova avaliação ao array de dados
        const novaAvaliacao = {
            id: result.avaliacaoId,
            professorId: professorSelecionado,
            disciplinaId: CLASS_ID,
            userEmail: currentUserEmail,
            nota: selectedRating,
            comentario: {
                id: result.comentarioId,
                texto: texto,
                nomeUsuario: result.nomeUsuario || 'Você',
                upVotes: 0,
                downVotes: 0,
                createdAt: new Date().toISOString(),
                isOwner: true,  // ✅ Usuário é o dono do comentário que acabou de criar
                hasVoted: 0  // ✅ Ainda não votou no próprio comentário
            },
            createdAt: new Date().toISOString()
        };
        
        // Adicionar aos dados globais
        AVALIACOES_DATA.push(novaAvaliacao);
        
        // Atualizar visualização baseado no contexto atual
        if (professorSelecionado === null) {
            // Avaliação da disciplina
            avaliacoesDisciplinaGlobal.push(novaAvaliacao);
            const stats = calcularStats(avaliacoesDisciplinaGlobal);
            atualizarDisciplina(stats, avaliacoesDisciplinaGlobal);
        } else {
            // Avaliação de professor específico
            const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
            const avaliacoesProf = avaliacoesProfessores.filter(a => 
                String(a.professorId) === String(professorSelecionado)
            );
            const stats = calcularStats(avaliacoesProf);
            
            // Encontrar índice do professor
            const profIndex = PROFESSORES_DATA.findIndex(p => {
                const profId = p.id || p.professorId || p.ID;
                return String(profId) === String(professorSelecionado);
            });
            
            if (profIndex !== -1) {
                atualizarProfessor(profIndex, stats);
            }
            
            // Atualizar lista de comentários
            const prof = PROFESSORES_DATA[profIndex];
            const profNome = prof?.nome || prof?.name || 'Professor';
            mostrarComentarios(avaliacoesProf, profNome);
        }
        
        alert('Avaliação publicada com sucesso!');
        
	} catch (error) {
		console.error('Erro ao enviar comentário:', error);
		alert('Erro ao enviar comentário: ' + error.message);
		return;
	}
    
    // Close editor and reset
    toggleCommentEditor();
}

// ============================================
// VALIDATION & INTERACTION FUNCTIONS
// ============================================

/**
 * Verifica se o usuário já avaliou o contexto atual (disciplina ou professor)
 */
function jaAvaliouContextoAtual() {
    if (!currentUserEmail) return false;
    
    // Verificar se já existe avaliação para (usuário, disciplina, professor)
    const jaExiste = AVALIACOES_DATA.some(av => {
        const mesmoUsuario = av.userEmail === currentUserEmail;
        const mesmaDisciplina = av.disciplinaId === CLASS_ID;
        
        // Se estamos avaliando a disciplina (sem professor)
        if (professorSelecionado === null) {
            return mesmoUsuario && mesmaDisciplina && !av.professorId;
        }
        
        // Se estamos avaliando um professor específico
        const mesmoProfessor = String(av.professorId) === String(professorSelecionado);
        return mesmoUsuario && mesmaDisciplina && mesmoProfessor;
    });
    
    return jaExiste;
}

/**
 * Votar em um comentário (upvote ou downvote)
 */
async function voteComment(comentarioId, isUpVote) {
    if (!currentUserEmail) {
        alert('Você precisa estar logado para votar.');
        return;
    }
    
    try {
		  const formData = new FormData();
   	  formData.append('isUpVote', isUpVote);
		  
		  console.log(`Votando no comentário ${comentarioId} - Upvote: ${isUpVote}`);

        const response = await fetch(`/api/comentarios/${comentarioId}/votar`, {
            method: 'POST',
            body: formData
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            alert('Erro ao votar: ' + errorText);
            return;
        }
        
        const result = await response.json();
        console.log('Voto registrado:', result);
        
        // Atualizar contadores no frontend
        const avaliacao = AVALIACOES_DATA.find(a => a.comentario?.id === comentarioId);
        if (avaliacao && avaliacao.comentario) {
            avaliacao.comentario.upVotes = result.upVotes || 0;
            avaliacao.comentario.downVotes = result.downVotes || 0;
            
            // ✅ Atualizar estado de voto (hasVoted)
            // Se o backend retornar o novo estado de voto
            if (result.userVote !== undefined) {
                // Backend pode retornar 1, -1, ou 0
                if (result.userVote === 0) {
                    avaliacao.comentario.hasVoted = 0;
                } else {
                    avaliacao.comentario.hasVoted = result.userVote;
                }
            } else {
                // Se backend não retornar, inferir baseado no clique
                // Se clicou no mesmo voto, remove (null), senão define o voto
                if (avaliacao.comentario.hasVoted === (isUpVote ? 1 : -1)) {
                    avaliacao.comentario.hasVoted = 0; // Toggle off
                } else {
                    avaliacao.comentario.hasVoted = isUpVote ? 1 : -1; // Set new vote
                }
            }
				console.log('Estado atualizado do comentário após voto:', avaliacao.comentario);
            
            // Atualizar visualização
            if (professorSelecionado === null) {
                const comComentario = avaliacoesDisciplinaGlobal.filter(a => a.comentario && a.comentario.texto);
                mostrarComentarios(comComentario, null);
            } else {
                const avaliacoesProfessores = AVALIACOES_DATA.filter(a => a.professorId);
                const avaliacoesProf = avaliacoesProfessores.filter(a => 
                    String(a.professorId) === String(professorSelecionado)
                );
                const prof = PROFESSORES_DATA.find(p => {
                    const profId = p.id || p.professorId || p.ID;
                    return String(profId) === String(professorSelecionado);
                });
                const profNome = prof?.nome || prof?.name || 'Professor';
                const comComentario = avaliacoesProf.filter(a => a.comentario && a.comentario.texto);
                mostrarComentarios(comComentario, profNome);
            }
        }
        
    } catch (error) {
        console.error('Erro ao votar:', error);
        alert('Erro ao votar: ' + error.message);
    }
}

/**
 * Responder a um comentário
 */
function replyToComment(comentarioId) {
    // TODO: Implementar sistema de respostas aninhadas
    alert(`Funcionalidade de resposta será implementada em breve.\nComentário ID: ${comentarioId}`);
    console.log('Reply to comment:', comentarioId);
}

/**
 * Editar um comentário existente
 */
function editComment(comentarioId, textoAtual, notaAtual) {
    // TODO: Implementar edição inline
    const novoTexto = prompt('Editar comentário:', textoAtual);
    
    if (novoTexto === null || novoTexto.trim() === '') {
        return; // Usuário cancelou
    }
    
    if (novoTexto === textoAtual) {
        alert('Nenhuma alteração foi feita.');
        return;
    }
    
    // TODO: Enviar para backend
    alert(`Funcionalidade de edição será implementada em breve.\nNovo texto: ${novoTexto.substring(0, 50)}...`);
    console.log('Edit comment:', comentarioId, 'New text:', novoTexto);
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
