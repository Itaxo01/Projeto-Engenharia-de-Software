/**
 * Página da Disciplina - JavaScript
 */

console.log('✅ class.js carregado');

let professorSelecionado = null;
let avaliacoesDisciplinaGlobal = [];

document.addEventListener('DOMContentLoaded', function() {
    if (typeof AVALIACOES_DATA === 'undefined') return;
    
    console.log('📊 Dados carregados:', {
        avaliacoes: AVALIACOES_DATA.length,
        professores: PROFESSORES_DATA?.length || 0
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
    
    lista.innerHTML = avaliacoes.map(a => `
        <div class="review-card">
            <div class="review-header">
                <div class="reviewer-info">
                    <div class="reviewer-avatar">${(a.comentario.nomeUsuario || 'A')[0].toUpperCase()}</div>
                    <span class="reviewer-name">${a.comentario.nomeUsuario || 'Usuário Anônimo'}</span>
                    <span class="reviewer-date">${formatarData(a.comentario.createdAt)}</span>
                </div>
                <div class="review-rating" data-nota="${a.nota}">
                    <div class="rating-stars">
                        ${'<span class="star">★</span>'.repeat(5)}
                    </div>
                </div>
            </div>
            <div class="review-content">${a.comentario.texto}</div>
        </div>
    `).join('');
    
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