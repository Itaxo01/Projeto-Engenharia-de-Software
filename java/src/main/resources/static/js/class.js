// Class page JavaScript

// Modal functions
function openReviewModal() {
    document.getElementById('reviewModal').style.display = 'block';
}

function closeReviewModal() {
    document.getElementById('reviewModal').style.display = 'none';
    resetReviewForm();
}

function resetReviewForm() {
    const form = document.querySelector('#reviewModal .modal-form');
    if (form) {
        form.reset();
        document.getElementById('selectedTags').innerHTML = '';
    }
}

// Tags functionality
const selectedTags = new Set();

function addTag(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        const input = event.target;
        const tag = input.value.trim();
        
        if (tag && !selectedTags.has(tag)) {
            selectedTags.add(tag);
            const tagElement = document.createElement('span');
            tagElement.className = 'tag removable';
            tagElement.textContent = tag;
            tagElement.onclick = () => removeTag(tag, tagElement);
            
            document.getElementById('selectedTags').appendChild(tagElement);
            input.value = '';
        }
    }
}

function removeTag(tag, element) {
    selectedTags.delete(tag);
    element.remove();
}

// Star rating functionality
document.addEventListener('DOMContentLoaded', function() {
    const ratingInputs = document.querySelectorAll('.rating-input input[type="radio"]');
    const ratingLabels = document.querySelectorAll('.rating-input label');
    
    ratingLabels.forEach((label, index) => {
        label.addEventListener('mouseover', function() {
            highlightStars(index);
        });
        
        label.addEventListener('mouseout', function() {
            resetStarHighlight();
        });
        
        label.addEventListener('click', function() {
            selectStars(index);
        });
    });
    
    function highlightStars(index) {
        ratingLabels.forEach((label, i) => {
            if (i >= index) {
                label.style.color = '#fbbf24';
            } else {
                label.style.color = '#d1d5db';
            }
        });
    }
    
    function resetStarHighlight() {
        const checkedInput = document.querySelector('.rating-input input[type="radio"]:checked');
        if (checkedInput) {
            const checkedIndex = Array.from(ratingInputs).indexOf(checkedInput);
            highlightStars(checkedIndex);
        } else {
            ratingLabels.forEach(label => {
                label.style.color = '#d1d5db';
            });
        }
    }
    
    function selectStars(index) {
        ratingInputs[index].checked = true;
        highlightStars(index);
    }
    
    // Review form submission
    const reviewForm = document.querySelector('#reviewModal .modal-form');
    if (reviewForm) {
        reviewForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const rating = document.querySelector('.rating-input input[type="radio"]:checked');
            const reviewText = document.getElementById('reviewText').value.trim();
            
            if (!rating) {
                alert('Selecione uma nota');
                return;
            }
            
            if (!reviewText) {
                alert('Escreva um comentário sobre a disciplina');
                return;
            }
            
            // TODO: Implement review submission
            const reviewData = {
                rating: rating.value,
                comment: reviewText,
                tags: Array.from(selectedTags)
            };
            
            console.log('Review data:', reviewData);
            alert('Funcionalidade de avaliação será implementada no backend');
            closeReviewModal();
        });
    }
});

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('reviewModal');
    if (event.target === modal) {
        closeReviewModal();
    }
}

// Dynamic class status color (could be used for real-time updates)
function updateClassStatus(status) {
    const statusElement = document.querySelector('.class-status');
    if (statusElement) {
        // Remove all status classes
        statusElement.classList.remove('available', 'unavailable', 'taken', 'evaluated', 'critical');
        // Add new status class
        statusElement.classList.add(status);
        
        // Update text based on status
        const statusTexts = {
            'available': 'Disponível',
            'unavailable': 'Indisponível',
            'taken': 'Cursada',
            'evaluated': 'Aprovado',
            'critical': 'Crítica'
        };
        
        statusElement.textContent = statusTexts[status] || status;
    }
}
