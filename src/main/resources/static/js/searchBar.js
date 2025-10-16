let searchTimeout;
const searchInput = document.getElementById('searchInput');
const searchResults = document.getElementById('searchResults');

searchInput.addEventListener('input', function() {
    const query = this.value.trim();
    
    // Limpa timeout anterior
    clearTimeout(searchTimeout);
    
    if (query.length < 2) {
        searchResults.innerHTML = '';
        searchResults.style.display = 'none';
        return;
    }
    
    // Debounce - aguarda 300ms após parar de digitar
    searchTimeout = setTimeout(() => {
        fetchDisciplinas(query);
    }, 800);
});

async function fetchDisciplinas(query) {
    try {
        const response = await fetch(`/api/search/class?query=${encodeURIComponent(query)}`);
        const disciplinas = await response.json();
        
        displayResults(disciplinas);
    } catch (error) {
        console.error('Erro ao buscar disciplinas:', error);
    }
}

function displayResults(disciplinas) {
    if (disciplinas.length === 0) {
        searchResults.innerHTML = '<div class="search-item no-results">Nenhuma disciplina encontrada</div>';
        searchResults.style.display = 'block';
        return;
    }
    
    const html = disciplinas.map(disc => `
        <div class="search-item" onclick="goToDisciplina('${disc.codigo}')">
            <div class="search-item-code">${disc.codigo}</div>
            <div class="search-item-name">${disc.nome}</div>
        </div>
    `).join('');
    
    searchResults.innerHTML = html;
    searchResults.style.display = 'block';
}

function goToDisciplina(codigo) {
    window.location.href = `/class/${codigo}`;
}

// Fecha resultados ao clicar fora
document.addEventListener('click', function(e) {
    if (!e.target.closest('.search-container')) {
        searchResults.style.display = 'none';
    }
});

// Previne fechar ao clicar dentro dos resultados
searchResults.addEventListener('click', function(e) {
    e.stopPropagation();
});