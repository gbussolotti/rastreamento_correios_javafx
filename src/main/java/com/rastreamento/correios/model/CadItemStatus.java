package com.rastreamento.correios.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gusta
 */
@Entity
@Table(name = "item_status")
@XmlRootElement
public class CadItemStatus implements Serializable, Comparable<CadItemStatus> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "tipo_evento")
    private String typeEvent;

    @Basic(optional = false)
    @Column(name = "data_evento", columnDefinition = "TIMESTAMP")
    private LocalDateTime dtEvent;

    @Basic(optional = false)
    @Column(name = "descricao_evento")
    private String eventDescription;

    @Column(name = "detalhe_evento")
    private String eventDetail;

    @Column(name = "local_evento")
    private String eventLocal;

    @Column(name = "cidade_evento")
    private String eventCity;

    @Column(name = "uf_evento")
    private String eventUf;

    @Column(name = "local_destino")
    private String destinationLocal;

    @Column(name = "cidade_destino")
    private String destinationCity;

    @Column(name = "bairro_destino")
    private String destinationBairro;

    @Column(name = "uf_destino")
    private String destinationUf;

    @Column(name = "cep_endereco")
    private String addressCep;

    @Column(name = "logradouro_endereco")
    private String addressStreet;

    @Column(name = "numero_endereco")
    private String addressNumber;

    @Column(name = "localidade_endereco")
    private String addressLocation;

    @Column(name = "uf_endereco")
    private String addressUf;

    @Column(name = "bairro_endereco")
    private String addressBairro;

    @JoinColumn(name = "item", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CadItem item;

    public CadItemStatus() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(String typeEvent) {
        this.typeEvent = typeEvent;
    }

    public LocalDateTime getDtEvent() {
        return dtEvent;
    }

    public void setDtEvent(LocalDateTime dtEvent) {
        this.dtEvent = dtEvent;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getEventLocal() {
        return eventLocal;
    }

    public void setEventLocal(String eventLocal) {
        eventLocal = eventLocal.equals("País") ? null : eventLocal;
        this.eventLocal = eventLocal;
    }

    public String getEventCity() {
        return eventCity;
    }

    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
    }

    public String getEventUf() {
        return eventUf;
    }

    public void setEventUf(String eventUf) {
        this.eventUf = eventUf;
    }

    public String getDestinationLocal() {
        return destinationLocal;
    }

    public void setDestinationLocal(String destinationLocal) {
        destinationLocal = destinationLocal.equals("País") ? null : destinationLocal;
        this.destinationLocal = destinationLocal;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getDestinationBairro() {
        return destinationBairro;
    }

    public void setDestinationBairro(String destinationBairro) {
        this.destinationBairro = destinationBairro;
    }

    public String getDestinationUf() {
        return destinationUf;
    }

    public void setDestinationUf(String destinationUf) {
        this.destinationUf = destinationUf;
    }

    public String getAddressCep() {
        return addressCep;
    }

    public void setAddressCep(String addressCep) {
        this.addressCep = addressCep;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }

    public String getAddressUf() {
        return addressUf;
    }

    public void setAddressUf(String addressUf) {
        this.addressUf = addressUf;
    }

    public String getAddressBairro() {
        return addressBairro;
    }

    public void setAddressBairro(String addressBairro) {
        this.addressBairro = addressBairro;
    }

    public CadItem getItem() {
        return item;
    }

    public void setItem(CadItem item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CadItemStatus)) {
            return false;
        }
        CadItemStatus other = (CadItemStatus) object;
        return other.getDtEvent().equals(this.dtEvent) && (other.getEventDescription() == null ? this.eventDescription == null : other.getEventDescription().equals(this.eventDescription));
    }

    @Override
    public String toString() {
        return "br.com.be.tipos.ItemStatus[ id=" + id + ", " + eventDescription + " ]";
    }

    @Override
    public int compareTo(CadItemStatus other) {
        if (this.getDtEvent().isBefore(other.getDtEvent())) {
            return 1;
        } else if (this.getDtEvent().isAfter(other.getDtEvent())) {
            return -1;
        } else {
            return 0;
        }
    }

}
