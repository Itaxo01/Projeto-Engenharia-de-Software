/**
 * Dashboard - Mapa Curricular
 */

console.log('✅ dashboard.js carregado');

let semestres = [];
const SEMESTRES_PADRAO = 8;
const MAX_DISCIPLINAS_POR_FASE = 6;

// Inicializar dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Criar semestres padrão (8 fases vazias)
    for (let i = 1; i <= SEMESTRES_PADRAO; i++) {
        semestres.push({
            numero: i,
            disciplinas: []
        });
    }
    
    renderizarSemestres();
});

// Renderizar grid de semestres
function renderizarSemestres() {
    const container = document.getElementById('semestresContainer');
    
    let html = '';
    
    semestres.forEach((sem, index) => {
        // Início da linha do semestre
        html += '<div class="semestre-row">';
        
        // Label da fase
        html += `<div class="fase-label">Fase ${sem.numero}</div>`;
        
        // Disciplinas existentes
        sem.disciplinas.forEach(disc => {
            html += criarCardDisciplina(disc);
        });
        
        // Botão adicionar (se tiver espaço)
        if (sem.disciplinas.length < MAX_DISCIPLINAS_POR_FASE) {
            html += `
                <div class="btn-add-disciplina-card" onclick="adicionarDisciplina(${index})" title="Adicionar disciplina">
                    +
                </div>
            `;
        }
        
        // Fim da linha do semestre
        html += '</div>';
    });
    
    container.innerHTML = html;
}

// Criar card de disciplina
function criarCardDisciplina(disciplina) {
    const statusClass = disciplina.status || 'indisponivel';
    return `
        <div class="disciplina-card ${statusClass}" onclick="abrirDisciplina('${disciplina.codigo}')">
            <div class="disciplina-codigo">${disciplina.codigo}</div>
            <div class="disciplina-nome">${disciplina.nome}</div>
        </div>
    `;
}

// Adicionar novo semestre
function adicionarSemestre() {
    const novoNumero = semestres.length + 1;
    semestres.push({
        numero: novoNumero,
        disciplinas: []
    });
    
    renderizarSemestres();
    console.log('Fase adicionada:', novoNumero);
}

// Adicionar disciplina a um semestre
function adicionarDisciplina(semestreIndex) {
    // TODO: Implementar modal de seleção de disciplina
    const codigo = prompt('Código da disciplina (ex: INE5401):');
    if (!codigo) return;
    
    const nome = prompt('Nome da disciplina:');
    if (!nome) return;
    
    // Selecionar status
    const status = prompt('Status (disponivel/indisponivel/cursada/avaliada/critica):', 'indisponivel');
    
    semestres[semestreIndex].disciplinas.push({
        codigo: codigo.toUpperCase(),
        nome: nome,
        status: status || 'indisponivel'
    });
    
    renderizarSemestres();
    console.log('Disciplina adicionada à fase', semestreIndex + 1);
}

// Abrir página da disciplina
function abrirDisciplina(codigo) {
    window.location.href = `/class/${codigo}`;
}

// Upload de currículo (placeholder)
function uploadCurriculo() {
    // TODO: Implementar upload de arquivo
    alert('Funcionalidade de upload será implementada em breve!');
    console.log('Upload de currículo/histórico solicitado');
}
