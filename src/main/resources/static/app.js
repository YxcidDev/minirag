async function ask() {
    const questionField = document.getElementById("question");
    const currentQuestion = document.getElementById("currentQuestion");
    const currentDate = document.getElementById("currentDate");
    const answerBox = document.getElementById("answer");
    const button = document.getElementById("askButton");

    const question = questionField.value.trim();

    if (!question) {
        answerBox.innerText = "Debe ingresar una pregunta.";
        return;
    }

    currentQuestion.textContent = question;
    currentDate.textContent = "—";
    answerBox.innerHTML = '<span class="loading">Consultando documentos...</span>';
    button.disabled = true;

    try {
        const response = await fetch("/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ question: question })
        });

        if (!response.ok) {
            throw new Error("HTTP error: " + response.status);
        }

        const data = await response.json();
        answerBox.innerText = data.answer;
        currentDate.textContent = formatDate(new Date());

        await loadHistory();
    } catch (error) {
        console.error(error);
        answerBox.innerText = "No fue posible obtener una respuesta.";
    } finally {
        button.disabled = false;
    }
}

function useQuestion(text) {
    document.getElementById("question").value = text;
}

function formatDate(value) {
    if (!value) {
        return "";
    }
    return new Date(value).toLocaleString("es-CO");
}

async function loadHistory() {
    const historyList = document.getElementById("historyList");

    try {
        const response = await fetch("/api/history");

        if (!response.ok) {
            throw new Error("HTTP error: " + response.status);
        }

        const items = await response.json();
        historyList.replaceChildren();

        if (items.length === 0) {
            historyList.textContent = "Aún no hay consultas registradas.";
            return;
        }

        // Newest first
        items.slice().reverse().forEach(item => {
            historyList.appendChild(buildHistoryItem(item));
        });
    } catch (error) {
        console.error(error);
        historyList.textContent = "No fue posible cargar el historial.";
    }
}

function buildHistoryItem(item) {
    const details = document.createElement("details");
    details.className = "history-item";

    const summary = document.createElement("summary");

    const questionSpan = document.createElement("span");
    questionSpan.className = "history-question";
    questionSpan.textContent = item.question;

    const dateSpan = document.createElement("span");
    dateSpan.className = "history-date";
    dateSpan.textContent = formatDate(item.date);

    summary.append(questionSpan, dateSpan);

    const answerDiv = document.createElement("div");
    answerDiv.className = "history-answer";
    answerDiv.textContent = item.answer;

    details.append(summary, answerDiv);
    return details;
}

document.addEventListener("DOMContentLoaded", loadHistory);