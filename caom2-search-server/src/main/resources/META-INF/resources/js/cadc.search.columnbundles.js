(function ($, window)
{
  $.extend(true, window, {
    "ca": {
      "nrc": {
        "cadc": {
          "search": {
            "ColumnBundleManager": ColumnBundleManager,
            "columnBundles": {
              "JCMT": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.target.name",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.energy.restwav",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.time.exposure",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Observation.type",
                  "caom2:Observation.intent",
                  "caom2:Observation.environment.tau",
                  "caom2:Observation.target.moving",
                  "caom2:Observation.algorithm.name",
                  "caom2:Plane.productID",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Observation.collection",
                  "caom2:Observation.observationID",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",                  
                  "caom2:Plane.publisherID"
	              ],
                "unitTypes": {
                  "caom2:Plane.energy.restwav": "GHz"
                },
                "size": 22
              },
              "CFHT": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.collection",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Plane.productID",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Observation.target.name",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Plane.time.exposure",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Observation.type",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Observation.observationID",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
	              ],
                "unitTypes": {
                  "caom2:Plane.energy.restwav": "m"
                },
                "size": 16
              },
              "CFHTMEGAWIR": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.observationID",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Observation.target.name",
                  "caom2:Plane.time.exposure",
                  "caom2:Plane.position.resolution",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.collection",
                  "caom2:Observation.instrument.name",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Plane.productID",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Observation.type",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",                  
                  "caom2:Plane.publisherID"
	              ],
                "unitTypes": {
                  "caom2:Plane.energy.restwav": "m"
                },
                "size": 11
              },
              "HST": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.collection",
                  "caom2:Observation.proposal.project",
                  "caom2:Observation.observationID",
                  "caom2:Plane.productID",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Observation.target.name",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Plane.time.exposure",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Observation.type",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Observation.environment.tau",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
                ],
                "unitTypes": {
                  "caom2:Plane.energy.restwav": "m"
                },
                "size": 16
              },
              "MOST": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Plane.productID",
                  "caom2:Observation.target.name",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.time.exposure",
                  "caom2:Observation.proposal.pi",
                  "caom2:Observation.proposal.id",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Observation.observationID",
                  "caom2:Observation.collection",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Observation.type",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.dataProductType",
                  "caom2:Plane.provenance.name",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.energy.restwav",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
                ],
                "unitTypes": {
                  "caom2:Plane.time.exposure": "DAYS"
                },
                size: 12
              },
              "DAO": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.observationID",
                  "caom2:Observation.target.name",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.time.exposure",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Observation.type",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.collection",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Plane.productID",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  
                  "caom2:Plane.publisherID"
                ],
                "size": 13
              },
              "DAOCADC": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.observationID",
                  "caom2:Plane.productID",
                  "caom2:Observation.target.name",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.time.exposure",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Observation.type",
                  "caom2:Observation.proposal.pi",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.collection",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
                ],
                "unitTypes": {
                  "caom2:Plane.energy.bounds.lower": "A",
                  "caom2:Plane.energy.bounds.upper": "A"
                },
                "size": 13
              },
              "DAOPLATES": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.observationID",
                  "caom2:Observation.target.name",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.time.exposure",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Observation.collection",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Observation.type",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Plane.productID",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
                ],
                "size": 10
              },
              "NEOSSAT": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.collection",
                  "caom2:Observation.observationID",
                  "caom2:Plane.productID",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.instrument.name",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Plane.time.exposure",
                  "caom2:Observation.target.name",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Observation.type",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.environment.tau",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
                ],
                "unitTypes": {
                  "caom2:Plane.energy.restwav": "m"
                },
                "size": 14
              },
              "DEFAULT": {
                "columns": [
                  "caom2:Observation.uri",
                  "caom2:Observation.collection",
                  "caom2:Observation.observationID",
                  "caom2:Plane.productID",
                  "caom2:Plane.position.bounds.cval1",
                  "caom2:Plane.position.bounds.cval2",
                  "caom2:Plane.time.bounds.lower",
                  "caom2:Observation.instrument.name",
                  "caom2:Plane.time.exposure",
                  "caom2:Observation.target.name",
                  "caom2:Plane.energy.bandpassName",
                  "caom2:Plane.calibrationLevel",
                  "caom2:Observation.type",
                  "caom2:Plane.energy.bounds.lower",
                  "caom2:Plane.energy.bounds.upper",
                  "caom2:Observation.proposal.id",
                  "caom2:Observation.proposal.pi",
                  "caom2:Plane.dataRelease",
                  "caom2:Plane.position.bounds.area",
                  "caom2:Plane.position.bounds",
                  "caom2:Plane.position.sampleSize",
                  "caom2:Plane.energy.resolvingPower",
                  "caom2:Plane.time.bounds.upper",
                  "caom2:Plane.dataProductType",
                  "caom2:Observation.target.moving",
                  "caom2:Plane.provenance.name",
                  "caom2:Plane.provenance.keywords",
                  "caom2:Observation.intent",
                  "caom2:Observation.target.type",
                  "caom2:Observation.target.standard",
                  "caom2:Observation.target.keywords",
                  "caom2:Plane.metaRelease",
                  "caom2:Observation.sequenceNumber",
                  "caom2:Observation.algorithm.name",
                  "caom2:Observation.proposal.title",
                  "caom2:Observation.proposal.keywords",
                  "caom2:Plane.position.resolution",
                  "caom2:Observation.instrument.keywords",
                  "caom2:Observation.environment.tau",
                  "caom2:Plane.energy.transition.species",
                  "caom2:Plane.energy.transition.transition",
                  "caom2:Observation.proposal.project",
                  "caom2:Plane.energy.emBand",
                  "caom2:Plane.provenance.reference",
                  "caom2:Plane.provenance.version",
                  "caom2:Plane.provenance.project",
                  "caom2:Plane.provenance.producer",
                  "caom2:Plane.provenance.runID",
                  "caom2:Plane.provenance.lastExecuted",
                  "caom2:Plane.provenance.inputs",
                  "caom2:Plane.energy.restwav",
                  "caom2:Observation.requirements.flag",
                  "caom2:Plane.id",
                  "caom2:Plane.publisherID.downloadable",
                  "caom2:Plane.publisherID"
	              ],
                "unitTypes": {
                  "caom2:Plane.energy.restwav": "m"
	              },
                "size": 13
              },
              "ObsCore": {
                "columns": [
                  "obscore:DataID.Collection",
                  "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C1",
                  "obscore:Char.SpatialAxis.Coverage.Location.Coord.Position2D.Value2.C2",
                  "obscore:Target.Name",
                  "obscore:Char.SpatialAxis.Coverage.Bounds.Extent.diameter",
                  "obscore:Char.SpatialAxis.Resolution.refval.value",
                  "obscore:Char.TimeAxis.Coverage.Bounds.Limits.StartTime",
                  "obscore:Char.TimeAxis.Coverage.Bounds.Limits.StopTime",
                  "obscore:DataID.observationID",
                  "obscore:Curation.PublisherDID",
                  "obscore:Provenance.ObsConfig.Facility.name",
                  "obscore:Provenance.ObsConfig.Instrument.name",
                  "obscore:Curation.releaseDate",
                  "obscore:ObsDataset.dataProductType",
                  "obscore:ObsDataset.calibLevel",
                  "obscore:Char.TimeAxis.Coverage.Support.Extent",
                  "obscore:Char.TimeAxis.Resolution.refval.value",
                  "obscore:Char.TimeAxis.numBins",
                  "obscore:Char.SpectralAxis.Coverage.Bounds.Limits.LoLimit",
                  "obscore:Char.SpectralAxis.Coverage.Bounds.Limits.HiLimit",
                  "obscore:Char.SpectralAxis.Resolution.ResolPower.refval",
                  "obscore:Char.SpectralAxis.numBins",
                  "obscore:Char.SpectralAxis.ucd",
                  "obscore:Char.SpatialAxis.numBins1",
                  "obscore:Char.SpatialAxis.numBins2",
                  "obscore:Char.PolarizationAxis.stateList",
                  "obscore:Char.PolarizationAxis.numBins",
                  "obscore:Char.ObservableAxis.ucd",
                  "obscore:Access.Reference",
                  "obscore:Access.Format",
                  "obscore:Access.Size",
                  "obscore:Char.SpatialAxis.Coverage.Support.Area",
                  "obscore:Curation.PublisherDID.downloadable"
                ],
                "size": 12
              }
            }
          }
        }
      }
    }
  });


  /**
   * Manages column bundles, e.g. retrieve the column bundle of a collection.
   *
   * @constructor
   */
  function ColumnBundleManager()
  {

    /**
     * Determine if all collections selected by the user are related
     * to CFHT or CFHTTERAPIX. 
     * 
     * @param {[]}  _collections An array of collections
     * @private
     */
    this._isCFHTBundle = function(_collections)
    {
      var isCFHT = true;
      for (var ci = 0, cl = _collections.length; ci < cl; ci++)
      {
        var v = _collections[ci];

        if (!((v === "CFHT") || (v === "CFHTTERAPIX")))
        {
          // contains a non-CFHT collection
          isCFHT = false;
          return isCFHT;
        }
      }

      return isCFHT;
    };

    /**
     * Determine if all collections selected by the user are related
     * to CFHTMEGAPIPE or CFHTWIRWOLF. 
     * 
     * @param {[]}  _collections An array of collections
     * @private
     */
    this._isCFHTMEGAWIRBundle = function(_collections)
    {
      var isCFHT = true;
      for (var ci = 0, cl = _collections.length; ci < cl; ci++)
      {
        var v = _collections[ci];

        if (!((v === "CFHTMEGAPIPE") || (v === "CFHTWIRWOLF")))
        {
          // contains a non-CFHT collection
          isCFHT = false;
          return isCFHT;
        }
      }

      return isCFHT;
    };

    /**
     * Determine the key and size of the column bundle object based on the 
     * collection(s) selected by the user.
     * 
     * @param {[]}  _collections An array of collections
     * @return {{}} a dictionary containing bundle name and number of columns in the bundle
     * @private
     */
    this._getColumnBundleInfo = function(_collections)
    {
      var key = "DEFAULT";

      if (_collections)
      {
        if (this._isCFHTBundle(_collections))
        {
          // user selected CFHT and/or CFHTTERAPIX collection(s) only,
          // use corresponding specific column bundle
          key = "CFHT";
        }
        else if (this._isCFHTMEGAWIRBundle(_collections))
        {
          // user selected CFHTMEGAPIPE and/or CFHTWIRWOLF collection(s) only,
          // use corresponding specific column bundle
          key = "CFHTMEGAWIR";
        }
        else if (_collections.length === 1)
        {
          // non-CFHT collection
          // user only selected one collection, use the collection name
          // as the key to obtain the collection specific bundle
          key = _collections[0];
        }
        else
        {
          // user selected more than one collection,
          // use non-collection specific column bundle,
          // use default column bundle
        }
      }

      return { key:key, size:this._getBundleObject(key).size };
    };

    /**
     * Retrieves the column bundle for a given collection.
     *
     * @param {[]} _collections An array of collections
     * @return {{}}   Array of columnIDs.
     */
    this.getBundle = function(_collections)
    {
      var bundleInfo = this._getColumnBundleInfo(_collections);
      return this._getBundleObject(bundleInfo.key);
    };

    /**
     * Retrieves the first size number of columns for a given collection.
     *
     * @param {String} bundleName Name of a column bundle
     * @param {Number} size Number of columns in the column bundle
     * @return {[]} Array of column IDs.
     */
    this.getColumnIDs = function(bundleName, size)
    {
      var bundle = this._getBundleObject(bundleName);
      return $(bundle["columns"]).slice(0, size); 
    };

    /**
     * Failsafe convenient method to retrieve the current bundle, or the
     * default one if none selected or matched the current name.
     *
     * @param {String} bundleName      The bundle name to look up.
     * @returns {{}}      Bundle selected, or default bundle.
     * @private
     */
    this._getBundleObject = function(bundleName)
    {
      return ca.nrc.cadc.search.columnBundles[bundleName] || ca.nrc.cadc.search.columnBundles["DEFAULT"];
    };

    /**
     * Retrieves the default columns of a given collection.
     *
     * @param {[]}  _collections An array of collections
     * @return {[]} Array of column IDs.
     */
    this.getDefaultColumnIDs = function(_collections)
    {
      // number of default columns for collections other than
      // those considered below
      var bundleInfo = this._getColumnBundleInfo(_collections);
      
      return this.getColumnIDs(bundleInfo.key, bundleInfo.size);
    };

    /**
     * Retrieves the default unit types of a given collection.
     *
     * @param {[]}  _collections An array of collections
     * @return {{}} Hash of column ID to unit types.
     */
    this.getDefaultUnitTypes = function(_collections)
    {
      var bundle = this.getBundle(_collections);

      return bundle["unitTypes"];
    };

    /**
     * Retrieves all of the columns of a given collection.
     *
     * @param {[]}  _collections An array of collections
     * @return {[]} Array of colum IDs.
     */
    this.getAllColumnIDs = function(_collections)
    {
      var bundle = this.getBundle(_collections);

      return bundle["columns"];
    };
  }

})(jQuery, window);
