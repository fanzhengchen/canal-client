

args := -Dmaven.test.skip=true -Pdev


define module_package
mvn --projects $(1) clean package $(args)
endef

define module_install
mvn --projects $(1) clean install $(args)
endef


fzc_package:kafka_config_install
	$(call module_package,fzc)


hive_client_package: kafka_config_install
	$(call module_package,hive-client)


kafka_config_install:clean_install
	@echo $(JAVA_HOME)
	$(call module_install,kafka-config)

print:
	@echo $(args)
	@echo $(PATH)


clean_install:
	mvn clean install $(args)
