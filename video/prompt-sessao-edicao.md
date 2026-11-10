Você vai editar sozinho, durante a madrugada, o vídeo-pitch do HoraControl IA
para o INOVA Senac. Não há ninguém acordado para aprovar etapas: trabalhe de forma
autônoma do começo ao fim e não pare para pedir OK.

## Teto de uso (regra mais importante)
- Antes de começar e depois de cada ato, consulte o uso do plano (ferramenta de
  uso / get_usage).
- Se o limite **semanal** chegar a **24%**, pare imediatamente: renderize o que
  estiver pronto e escreva o relatório final. Nunca passe de 24% no semanal.
- Economize: não renderize o vídeo inteiro várias vezes. Para conferir, gere só
  2 ou 3 quadros (stills) por ato, em resolução reduzida. Faça uma única
  renderização final em MP4.
- Se o limite de 5 horas acabar, apenas aguarde a renovação e continue.

## Materiais (já estão no repositório C:\horacontrol)
- Vídeo da webcam com a narração: `video/public/` (o arquivo de vídeo que estiver
  lá). É uma ou poucas tomadas; pode haver frases repetidas: mantenha sempre a
  última tomada de cada frase e corte erros, repetições e pausas longas.
- Prints do sistema: `video/public/prints/` (`01-login.png`, `02-turma.png`...).
- Roteiro final, narração e animações de cada ato: seção "ROTEIRO (versão final)"
  de `video/prompt-horacontrol-video.md`. Use também as seções 1 e 2 desse
  arquivo (identidade visual e direção de movimento). Ignore lá o que fala de
  Playwright, gravação de tela, arquivos de áudio separados e aprovação ato por
  ato: aqui são prints e um vídeo único.
- Identidade visual: variáveis `--hc-*` em
  `sistemaponto/src/main/resources/static/css/app.css` e o fundo
  `sistemaponto/src/main/resources/static/img/hora-control-background-v2.png`.

## Como montar
1. Crie o projeto Remotion em `video/` (package.json próprio, sem tocar no app).
   1920x1080, 30 fps.
2. Transcreva o vídeo com timestamps por palavra usando
   `@remotion/install-whisper-cpp` (modelo `small`, idioma português). Salve a
   transcrição em `video/src/transcricao.json`.
3. Monte a lista de cortes (trechos mantidos) a partir da transcrição e do
   roteiro. A duração final tem que ficar **abaixo de 2:55**.
4. Layout:
   - Webcam em tela cheia no gancho (0:00), na apresentação ("Foi aí que nós...")
     e no fechamento.
   - Nos demais trechos, webcam pequena num cartão arredondado no canto inferior
     direito, e as animações do roteiro ocupando a tela, sincronizadas pelas
     palavras-chave da transcrição.
   - Ato 3: prints dentro de mockups de notebook e celular, com zoom suave nos
     pontos citados, cursor animado e destaque no botão "Registrar entrada".
   - Ato 4 (IA, geolocalização): telas desenhadas no estilo do sistema, com o
     selo "PRÓXIMAS VERSÕES" sempre visível.
   - Legendas embutidas a partir da transcrição (texto branco, fundo escuro
     translúcido, parte de baixo da tela).
5. Sem trilha sonora nem efeitos (não há arquivos de áudio licenciados aqui); só
   a voz original. Deixe em `video/src/sfx.ts` uma lista de onde entrariam trilha
   e efeitos, para os alunos adicionarem depois se quiserem.
6. Todos os textos, tempos e cores centralizados em constantes.

## Entrega
- MP4 final em `video/out/horacontrol-pitch.mp4` (H.264, qualidade alta).
- `video/RELATORIO.md` com: duração final, o que foi feito, o que ficou faltando
  (se o teto de uso foi atingido), uso semanal no início e no fim, e os comandos
  para abrir o Remotion Studio e renderizar de novo.
- Não faça commit nem push.
