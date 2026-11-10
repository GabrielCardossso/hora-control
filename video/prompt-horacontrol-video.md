Você é um motion designer sênior + engenheiro de vídeo. Vamos produzir o vídeo
de pitch do HoraControl (2:45) usando Remotion neste repositório. O roteiro
completo está no final deste prompt: siga os 6 atos, os tempos, a narração
e as animações descritas.

## 1. Identidade visual (fidelidade total)
Antes de qualquer coisa, leia o código do HoraControl e extraia o design real:
paleta, tipografia, raios, sombras, espaçamento, ícones, tom dos textos.
O app é Spring Boot + Thymeleaf (não tem Tailwind). Os tokens estão nas
variáveis CSS `--hc-*` de `sistemaponto/src/main/resources/static/css/app.css`
(tema claro e escuro), as telas em `sistemaponto/src/main/resources/templates/`
e as imagens (fundo azul `hora-control-background-v2.png`, logo Senac) em
`sistemaponto/src/main/resources/static/img/`.
Crie video/src/video/tokens.ts com esses valores e use SOMENTE eles. Nada de cor
ou fonte inventada. Fundo azul do HoraControl na abertura; roxo só no momento
"HoraControl IA" (próximas versões), como o roteiro pede.
Me mostre um resumo dos tokens que encontrou antes de seguir.

## 2. Direção de movimento (sem cara de IA)
- Nada de gradiente roxo-azul genérico, glassmorphism por padrão, emoji,
  neon exagerado ou "partículas flutuando" sem motivo.
- Todo movimento tem motivo narrativo e sincroniza com a fala.
- Easing customizado (cubic-bezier / spring com damping alto). Proibido
  linear e bounce exagerado. Use antecipação, overshoot sutil e settle.
- Stagger entre elementos (60–120 ms), hierarquia de movimento clara:
  um elemento principal por vez, o resto em segundo plano.
- Tipografia cinética com tracking e kerning cuidados, sem animar letra
  por letra em tudo, só no gancho.
- Profundidade com sombras suaves e parallax leve; textura sutil (grain
  de 2–3%) pra tirar o aspecto digital chapado.
- Transições entre atos com intenção (match cut, wipe com forma do logo,
  zoom contínuo), nunca fade genérico.
- Formato: 1920x1080 (16:9), 30 fps, safe margins, textos legíveis em celular.

## 3. Telas reais do produto
Quando precisar mostrar o sistema (Ato 3):
1. Suba o projeto localmente, em ambiente de desenvolvimento: em `sistemaponto/`,
   `.\mvnw.cmd spring-boot:run` (Windows) com PostgreSQL local, conforme o
   README. Acesse http://localhost:8080. Nunca use produção nem dados reais.
2. Crie dados de exemplo realistas (turmas como "TDS A", professor, registros).
3. Grave com Playwright (recordVideo):
   - Desktop: viewport 1920x1080.
   - Celular: viewport 390x844.
   Use cursor animado visível, cliques com ripple e ritmo humano (sem pressa).
4. Salve em video/public/footage/ e coloque dentro de mockups de notebook e
   celular no Remotion, com zoom nos pontos que o roteiro cita (seletor de
   turma, botão "Registrar entrada", tela de ajuste, relatório PDF).
Se uma tela da versão IA ainda não existir, desenhe como mockup fiel ao design
do produto e mantenha o rótulo claro de "próximas versões".

## 4. Áudio
- Narração: vou fornecer um arquivo por ato em video/public/audio/
  (ato1.mp3 ... ato6.mp3), gravados com a nossa própria voz (o regulamento do
  INOVA exige que o pitch seja apresentado pelos alunos; nada de voz gerada por
  IA). Se ainda não existirem, use placeholder e deixe os tempos
  parametrizados. Meça a duração de cada arquivo, transcreva com timestamps por
  palavra e sincronize as cenas pelas palavras-chave da fala.
- Trilha: instrumental sem direitos autorais, volume baixo, com ducking
  automático sob a narração. Energia sobe no Ato 4 e resolve no Ato 6.
- SFX (liste em video/src/video/sfx.ts com timestamp, arquivo e volume): whoosh
  nas transições, "tick" de relógio no gancho, clique suave nos cliques do
  cursor, cadeado fechando, carimbo "Justificado · Auditado", "pop" nos cartões
  de perfil, ding de notificação no celular, contador subindo no −70%.
  SFX discretos, mixados abaixo da voz.
- Me diga quais arquivos de áudio preciso baixar/gerar, com termo de busca
  sugerido e licença aceitável.

## 5. Estrutura e entrega
- Tudo dentro da pasta video/ (package.json próprio), sem alterar o app.
- Um componente por ato (Ato1Conflito.tsx ... Ato6Final.tsx) + Root.tsx.
- Todos os textos, tempos e cores centralizados em constantes.
- Rode o Remotion Studio pra eu revisar.
- Trabalhe ato por ato: termine um, me mostre, espere meu OK antes do próximo.
- No final, renderize MP4 (H.264, qualidade alta) e me dê o comando.
- Slots pra foto/vídeo de Gabriel e Caroline no Ato 2 (o regulamento pede que
  os alunos apareçam; não pode ser só animação) e nos créditos do Ato 6.
- Duração final obrigatoriamente abaixo de 3:00 (acima disso a proposta é
  desclassificada).

Comece pelo passo 1 (tokens) e pelo plano geral de timeline. Não escreva
animação ainda.

=== ROTEIRO (versão final) ===

0:00 – 0:34 · ATO 1 · O CONFLITO (arquivo ato1.mp3)

[0:00 – 0:11] O gancho
NARRAÇÃO: Fim de mês. Um relatório com centenas de linhas. Um horário em
branco. Uma aula que ninguém lembra se terminou às dez... ou às dez e meia.
ANIMAÇÃO: Abre no fundo azul do HoraControl. "Fim de mês." entra em tipografia
cinética, letra por letra. Um relatório extenso surge em perspectiva 3D e vai
rolando; as linhas passam rápido. Uma célula vazia pisca em vermelho. Um relógio
aparece com o ponteiro indeciso entre 22:00 e 22:30.

[0:11 – 0:34] O problema
NARRAÇÃO: É assim que os professores do Senac Tubarão controlam suas horas:
tudo à mão. Esquecimentos, erros de digitação e um retrabalho enorme para
corrigir tudo no fim do mês, sem nenhuma garantia de que a correção estava
certa. E quando o professor recebe por hora, cada erro de horário vira erro de
pagamento.
ANIMAÇÃO: Ícone de professor no centro, com uma mão escrevendo horários num
papel ("tudo à mão"). As palavras "Esquecimentos", "Erros de digitação" e
"Retrabalho" caem do alto e se empilham, cada uma no ritmo da fala. Calendário
avançando até o dia 30, com uma pilha de correções crescendo e um ponto de
interrogação sobre ela. Fecha com a equação animada: "erro de horário = erro de
pagamento", e um R$ riscado em vermelho.

0:34 – 0:47 · ATO 2 · OS HERÓIS · A virada (arquivo ato2.mp3)
NARRAÇÃO: Foi aí que nós, Gabriel Cardoso e Caroline Vargas, alunos do Técnico
em Desenvolvimento de Sistemas do Senac Tubarão, decidimos resolver esse
problema de dentro. E assim nasceu o HoraControl.
ANIMAÇÃO: O relatório se desfaz em pixels, que voam e se reorganizam formando o
logo do HoraControl. Entram foto ou vídeo curto do Gabriel e da Caroline, com
os nomes em "lower third". Selo "Senac Tubarão · Técnico em Desenvolvimento de
Sistemas".

0:47 – 1:24 · ATO 3 · A SOLUÇÃO · O que o HoraControl já faz (arquivo ato3.mp3)
NARRAÇÃO: O HoraControl é um sistema web de controle de ponto feito para
instituições de ensino. O professor escolhe a turma e registra a entrada, no
computador ou no celular, com o horário do servidor, que ninguém consegue
alterar. São três perfis: o professor, o coordenador, que acompanha os
professores do seu curso, e o gestor, que administra tudo. Precisa corrigir um
ponto? Só o gestor ajusta, sempre com justificativa e histórico de quem mudou e
quando. E o relatório do mês sai em PDF, em segundos.
ANIMAÇÃO: Mockup de notebook e celular flutuando, com as gravações reais das
telas dentro. Zoom no seletor de turma e no botão "Registrar entrada", com
cursor animado clicando. Um cadeado aparece ao lado do relógio ("horário do
servidor"). Três cartões deslizam lado a lado: Professor, Coordenador e Gestor,
cada um com seu ícone. Tela de ajuste com o carimbo "Justificado · Auditado"
batendo sobre o registro. Um PDF sai voando da tela de relatórios.

1:24 – 2:17 · ATO 4 · O FUTURO (arquivo ato4.mp3)

[1:24 – 1:58] O HoraControl IA
NARRAÇÃO: Mas registrar a hora é só o começo. A gente quer que o erro nem chegue
a acontecer. Por isso, nas próximas versões, vem o HoraControl IA. A
Inteligência Artificial vai aprender o ritmo de cada turma e avisar no mesmo dia
quando um ponto ficar esquecido aberto. Para corrigir, o professor só escreve:
"esqueci de bater a saída, saí às dez". A IA monta o pedido, e o gestor aprova
com um clique. Quem decide é sempre uma pessoa.
ANIMAÇÃO: Transição forte: selo "PRÓXIMAS VERSÕES" e o logo vira "HoraControl
IA", com brilho roxo. Notificação no celular: "Seu ponto da turma TDS A ainda
está aberto". Balão de chat sendo digitado; a IA transforma o texto num cartão
de pedido de ajuste; o gestor clica em "Aprovar" e aparece um ✓. Na frase "Quem
decide é sempre uma pessoa", destaque no ícone do gestor.

[1:58 – 2:17] Geolocalização e muito mais
NARRAÇÃO: Com a geolocalização, o ponto só vale quando o professor está, de
fato, dentro da unidade. E o gestor vai poder perguntar, "quais turmas estão
abaixo da carga horária?", e receber a resposta na hora. E muito mais:
notificações automáticas, previsão das horas do mês.
ANIMAÇÃO: Mapa com o pin do Senac Tubarão e um círculo de área ao redor; o ícone
do professor entra no círculo e o ponto fica verde. Caixa de pergunta sendo
digitada e um gráfico de barras crescendo como resposta. Fecha com uma grade de
ícones acendendo: notificações, previsão das horas, "e muito mais".

2:17 – 2:35 · ATO 5 · A RECOMPENSA · O impacto (arquivo ato5.mp3)
NARRAÇÃO: Nossa meta no piloto, aqui no Senac Tubarão, é reduzir as correções de
fim de mês e cortar pelo menos setenta por cento do tempo de fechamento das
horas. Tudo isso com um custo de poucos reais por professor, por mês.
ANIMAÇÃO: Pilha de correções diminuindo, com uma seta para baixo. Contador
animado: −70% no tempo de fechamento das horas. Moeda pequena com "poucos reais
por professor/mês".

2:35 – 2:45 · ATO 6 · A MENSAGEM FINAL · O fechamento (arquivo ato6.mp3)
NARRAÇÃO: Começa aqui, em Tubarão. E, no futuro, todas as instituições que vivem
esse mesmo problema. HoraControl IA: o controle de horas mais inteligente.
ANIMAÇÃO: Mapa: um ponto acende em Tubarão, depois em Santa Catarina, depois
vários pelo Brasil. Logo "HoraControl IA" grande, com "o controle de horas mais
inteligente" aparecendo embaixo. Créditos: Gabriel Cardoso Corrêa Marcos ·
Caroline Vargas da Silva · Senac Tubarão.
