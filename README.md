# Dino Evolution Roleplay

Mod para Minecraft Java 1.20.1 (Forge 47.3.0) com GeckoLib 4.

## Como compilar online (GitHub Actions)

1. Crie um repositório novo no GitHub (sem README).
2. Suba **todo o conteúdo desta pasta** (inclusive a pasta oculta `.github`).
3. Abra a aba **Actions**. A build começa sozinha a cada push na branch `main`.
4. Quando ficar verde, abra a execução e baixe o `.jar` em **Artifacts**.
5. Coloque o `.jar` na pasta `mods`, junto com o **GeckoLib 4.4.x para 1.20.1**.

Não precisa de `gradlew`: o workflow instala o Gradle 8.1.1 sozinho.

## Testando no jogo

Com cheats ligados:

- `/dino morph stegosaurus | brontosaurus | raptor_pink | raptor_yellow | ankylosaurus`
- `/dino level 1..5`
- `/dino reset`
- Tecla `R`: Dash (gasta fome, 3 s de recarga)

Os modelos, texturas e animações em `src/main/resources/assets/dinoevolution/` são
**placeholders de blocos**. Substitua pelos seus do Blockbench mantendo os nomes dos arquivos
e as animações `idle`, `walk`, `run` e `swim`.
