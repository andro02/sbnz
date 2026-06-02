import L from "leaflet";
import "leaflet.markercluster";

import { useEffect } from "react";
import { useMap } from "react-leaflet";
import { makePinIcon } from "../utils/makePinIcon";
import { makeClusterIcon } from "../utils/makeClusterIcon";

import "leaflet.markercluster/dist/MarkerCluster.css";
import "leaflet.markercluster/dist/MarkerCluster.Default.css";
import "../css/MarkerCluster.css"

const sanitaryIcon = makePinIcon("#2E7D32", "♻️");
const unsanitaryIcon = makePinIcon("#d18135ff", "☣️");

function MarkerCluster({ landfills, registryLandfills, handleMarkerClick, layersDetected, layersRegistry }) {
  const map = useMap();

  useEffect(() => {
    if (!map) return;

    const detectedClusters = L.markerClusterGroup({ iconCreateFunction: makeClusterIcon("detected") });

    if (Object.values(layersDetected).some(v => v === true))
      landfills.forEach((lf) => {
        if((lf.status === "Sanitary" && !layersDetected.sanitary) || (lf.status !== "Sanitary" && !layersDetected.unsanitary)) return;
        const marker = L.marker([lf.centerLat, lf.centerLon], { icon: lf.status === "Sanitary" ? sanitaryIcon : unsanitaryIcon })
          .on("click", () => handleMarkerClick(lf.id, map, "detected"));

        detectedClusters.addLayer(marker);
      });

    map.addLayer(detectedClusters);

    return () => {
      map.removeLayer(detectedClusters);
    }
  }, [landfills, map, handleMarkerClick, layersDetected, layersRegistry]);

  return null;
}

export default MarkerCluster;