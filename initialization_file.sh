#!/bin/bash

# Captura o texto selecionado
TEXT=$(xclip -o)

# Obtém o nome da janela atualmente em foco
FOCUSED_WINDOW=$(xprop -id "$(xprop -root _NET_ACTIVE_WINDOW | awk '{print $5}')" WM_NAME | grep -Eo '"[^"]*"$' | sed 's/"//g')

java -jar <path_to_repo>/ChatApp/target/ChatApp-1.0-SNAPSHOT.jar "$FOCUSED_WINDOW" "$TEXT" &
