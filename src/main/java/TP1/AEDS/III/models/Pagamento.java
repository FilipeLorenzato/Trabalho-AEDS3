package TP1.AEDS.III.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private Boleto idBoleto;
    private Long id;
    private LocalDateTime dataPagamento;
    private BigDecimal valorASerPago;

    public Pagamento() {
        
    }

    public Pagamento(Boleto idBoleto, Long id, LocalDateTime dataPagamento, BigDecimal valorASerPago) {
        this.idBoleto = idBoleto;
        this.id = id;
        this.dataPagamento = dataPagamento;
        this.valorASerPago = valorASerPago;
    }

    //setters
    public void setIdBoleto(Boleto idBoleto) { this.idBoleto = idBoleto; }

    public void setId(Long id) { this.id = id; }

    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public void setValorASerPago(BigDecimal valorASerPago) { this.valorASerPago = valorASerPago; }

    //getters
    public Boleto getIdBoleto() { return idBoleto; }

    public Long getId() { return id; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }

    public BigDecimal getValorASerPago() { return valorASerPago; }
}
