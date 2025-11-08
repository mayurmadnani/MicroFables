# MicroFables

MicroFables is an Android (Kotlin) application plus a lightweight local training setup for generating and exploring micro‑stories (fables, horror flashes, sci‑fi vignettes, etc.). It provides:
- A chat UI for iteratively prompting a local or remote LLM (via an Ollama‑style API service).
- Curated JSONL story datasets for experimentation and fine‑tuning.
- A Jupyter notebook pipeline for formatting, training, and evaluating story / reasoning models.

---

## Demo

🧸 Wholesome Short Stories

https://github.com/user-attachments/assets/adccf53c-bf8d-4f73-9c24-245bbd93b8b7

🎭 Dialogue-Only Stories

https://github.com/user-attachments/assets/33e2fd47-644c-4db6-956a-b22ce4777239

🌸 Haiku Stories

https://github.com/user-attachments/assets/c55cdd6c-fe55-45be-bdcb-d5defdfe5511

🎃 Horror Micro-Tales

https://github.com/user-attachments/assets/bd71db39-dec1-49c7-ae26-1e444a4f1401

📜 Letter/Diary Format

https://github.com/user-attachments/assets/4aa22f3f-4d39-4967-8ad5-8a1d53f39679

🦊 Moral Fables

https://github.com/user-attachments/assets/2ac59414-7fca-44f0-a2b0-010ec9f3f70c

🕵️ Mystery Puzzles

https://github.com/user-attachments/assets/421be6d8-eb27-4fce-9ac6-acfa40e53bc8

🗞️ News Report Style

https://github.com/user-attachments/assets/d66280a2-4e7c-4013-846c-6ef7f3ca1940

💞 Romance Vignettes

https://github.com/user-attachments/assets/cca3dcb5-5982-476d-a7e2-5cf48afe902e

🚀 Sci-Fi Flash Fiction

https://github.com/user-attachments/assets/293b343b-8591-470d-a041-46905d5aa49e

### Links

[Ollama Microfables Models](https://ollama.com/mayurmadnani/gemma-3-270m-microfables)

[HuggingFace Microfables Models](https://huggingface.co/mayurmadnani/models?search=microfables)


## 1. Core Features

| Area | Highlights |
|------|------------|
| Chat UI | Bubble layout for User / AI messages; model selection spinner; send action |
| Model Interaction | Pluggable API client via [`OllamaApiService`](app/src/main/java/mayurmadnani/microfables/OllamaApiService.kt) |
| Story Datasets | Multiple thematic JSONL files under [`datasets/`](datasets) |
| Training | Notebook‑based pipeline in [`model/Training.ipynb`](model/Training.ipynb) |
| Theming | Material design + custom color tokens (`@color/primary_500`, etc.) |
| Extensibility | Add new datasets, adapt API endpoints, customize bubbles, integrate model selection |


## 2. Project Structure

```
MicroFables/
  gradle.properties
  app/
    build.gradle.kts
    src/main/
      AndroidManifest.xml
      java/mayurmadnani/microfables/
        MainActivity.kt
        ChatAdapter.kt
        ChatMessage.kt
        OllamaApiService.kt
      res/
        layout/ (activity_main.xml, item_chat_ai.xml, item_chat_user.xml, ...)
        drawable/ (chat_bubble_ai.xml, chat_bubble_user.xml, ...)
        values/ (colors.xml, strings.xml, themes.xml, dimens.xml)
        xml/ (network_security_config.xml, backup_rules.xml, data_extraction_rules.xml)
  datasets/
    stories_dataset.jsonl (aggregate) + themed subsets
  model/
    Training.ipynb
```

Key Kotlin classes:
- [`MainActivity`](app/src/main/java/mayurmadnani/microfables/MainActivity.kt) – Activity wiring UI, RecyclerView, spinner, send action.
- [`ChatAdapter`](app/src/main/java/mayurmadnani/microfables/ChatAdapter.kt) – Binds message list to different bubble layouts.
- [`ChatMessage`](app/src/main/java/mayurmadnani/microfables/ChatMessage.kt) – Data model for each chat entry.
- [`OllamaApiService`](app/src/main/java/mayurmadnani/microfables/OllamaApiService.kt) – API abstraction layer for model inference.


## 3. Datasets

Located in [datasets/](datasets):
- Each line in the dataset is a JSON object { "instruction": ..., "response": ... }
- Thematic JSONL sets: horror, sci_fi_flash_fiction, romance_vignettes, moral, mystery, dialogue_only, letter_diary, news_report, haiku, plus umbrella `stories_dataset.jsonl`.
```
├── dialogue_only_stories_dataset.jsonl
├── haiku_stories_dataset.jsonl
├── horror_stories_dataset.jsonl
├── letter_diary_stories_dataset.jsonl
├── moral_stories_dataset.jsonl
├── mystery_stories_dataset.jsonl
├── news_report_stories_dataset.jsonl
├── romance_vignettes_stories_dataset.jsonl
├── sci_fi_flash_fiction_dataset.jsonl
└── stories_dataset.jsonl
```

Typical usage:
```python
import json
stories = []
with open("datasets/moral_stories_dataset.jsonl") as f:
    for line in f:
        stories.append(json.loads(line))
```

## 4. Model Training Notebook

Notebook: [model/Training.ipynb](model/Training.ipynb)

Capabilities:
- Load a dataset (`stories_dataset.jsonl`).
- Format examples for instruction/chat style fine‑tuning.
- Run quick experiments (few steps) or longer training loops.

Example cell (conceptual):
```python
from datasets import load_dataset
dataset = load_dataset("json", data_files="datasets/stories_dataset.jsonl")
print(dataset)
```

You can adapt formatting to match the app’s chat turn structure:
```
<start_of_turn>user
PROMPT
<end_of_turn>
<start_of_turn>model
STORY
<end_of_turn>
```

---

Happy storytelling & experimentation !