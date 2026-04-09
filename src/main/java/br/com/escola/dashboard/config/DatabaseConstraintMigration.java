package br.com.escola.dashboard.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConstraintMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseConstraintMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF EXISTS (
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_name = 'cards'
                    ) THEN
                        UPDATE cards
                        SET categoria = 'AVISO_NOTA'
                        WHERE categoria = 'AVISO';

                        UPDATE cards
                        SET categoria = 'ROTINA_ADMINISTRATIVA'
                        WHERE categoria = 'TAREFA';

                        IF EXISTS (
                            SELECT 1
                            FROM information_schema.table_constraints
                            WHERE table_name = 'cards'
                              AND constraint_name = 'cards_categoria_check'
                        ) THEN
                            ALTER TABLE cards DROP CONSTRAINT cards_categoria_check;
                        END IF;

                        ALTER TABLE cards
                        ADD CONSTRAINT cards_categoria_check
                        CHECK (
                            categoria IN (
                                'EVENTO',
                                'FALTA_PROFESSOR',
                                'HORARIO_PROFESSOR',
                                'ROTINA_ADMINISTRATIVA',
                                'ROTINA_AUXILIAR',
                                'AVISO_NOTA'
                            )
                        );
                    END IF;
                END $$;
                """);
    }
}
