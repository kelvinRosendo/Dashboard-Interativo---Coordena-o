package br.com.escola.dashboard.enums;

/**
 * 🔹 StatusCard
 *
 * Esse enum define os possíveis status de um card dentro do sistema.
 *
 * Cada card/evento só poderá ter um desses valores.
 */
public enum StatusCard {

    /**
     * 🔹 PENDENTE
     *
     * O item ainda não começou
     * ou ainda está aguardando ação.
     */
    PENDENTE,

    /**
     * 🔹 EM_ANDAMENTO
     *
     * O item está sendo preparado,
     * executado ou acompanhado.
     */
    EM_ANDAMENTO,

    /**
     * 🔹 CONCLUIDO
     *
     * O item já foi finalizado.
     */
    CONCLUIDO
}