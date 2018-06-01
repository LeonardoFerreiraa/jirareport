package br.com.leonardoferreira.jirareport.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.leonardoferreira.jirareport.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author lferreira
 * @since 5/7/18 6:49 PM
 */
@Data
@Entity
@ToString(exclude = "board")
@EqualsAndHashCode(of = {"date", "board"}, callSuper = false)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"date", "board_id"}))
public class Holiday extends BaseEntity {

    private static final long serialVersionUID = 18640912961216513L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data deve ser informada.")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @NotEmpty(message = "A descrição deve ser informada.")
    private String description;

    @ManyToOne
    private Board board;

    @Transient
    public String getEnDate() {
        return date == null ? null : date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", DateUtil.LOCALE_BR));
    }

}
