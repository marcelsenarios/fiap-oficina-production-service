# language: pt
Funcionalidade: Gestão e Ciclo de Vida da Esteira de Produção
  Como mecânico responsável da oficina
  Quero controlar a esteira de execução de uma ordem de serviço
  Para garantir a rastreabilidade técnica dos reparos até a entrega final

  Cenário: Execução completa de reparos veiculares com sucesso
    Dado que uma ordem de produção foi enfileirada para a OS de ID 10 e número "OS-2024-001"
    E a placa do veículo é "BRA2E19" e modelo "Corolla 2.0"
    Quando o mecânico "Silvio Santos" com matrícula "MEC-01" inicia o diagnóstico veicular
    Então o status da ordem de produção deve ser "EM_DIAGNOSTICO"
    Quando o mecânico inicia a execução física dos reparos
    Então o status da ordem de produção deve ser "EM_EXECUCAO"
    Quando o mecânico adiciona o apontamento técnico "Substituído jogo de velas e filtro de combustível"
    E o mecânico inicia os testes de qualidade
    Então o status da ordem de produção deve ser "EM_TESTES"
    Quando o mecânico finaliza a produção com a nota "Veículo testado e aprovado"
    Então o status final da ordem de produção deve ser "FINALIZADO"
    E a ordem de produção deve conter 5 apontamentos registrados

  Cenário: Tratamento de falha técnica na esteira mecânica para rollback do Saga
    Dado que uma ordem de produção foi enfileirada para a OS de ID 20 e número "OS-2024-002"
    E a placa do veículo é "XYZ9A88" e modelo "Civic 1.5"
    Quando o mecânico "Silvio Santos" com matrícula "MEC-01" inicia o diagnóstico veicular
    Quando o mecânico reporta uma falha técnica com o motivo "Bloco do motor trincado sem conserto"
    Então o status final da ordem de produção deve ser "FALHA_TECNICA"
    E o motivo registrado da falha deve ser "Bloco do motor trincado sem conserto"
