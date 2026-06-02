import { useEffect, useState, useRef } from "react";
import { MapContainer, TileLayer } from "react-leaflet";
import axios from "axios";
import "leaflet/dist/leaflet.css";
import MarkerCluster from "./MarkerCluster";
import LandfillInfoPanel from "./LandfillInfoPanel";

function LandfillMap() {
  const [landfills, setLandfills] = useState([]);
  const [selectedLandfill, setSelectedLandfill] = useState(null);
  const [evaluation, setEvaluation] = useState(null);
  const [panelOpen, setPanelOpen] = useState(false);
  const mapRef = useRef(null);

  useEffect(() => {
    axios.get("/api/landfills")
      .then(res => setLandfills(res.data))
      .catch(err => console.error(err));
  }, []);

  const handleMarkerClick = async (id) => {
    try {
      const res = await axios.get(`/api/landfills/${id}`);
      setSelectedLandfill(res.data);
      setPanelOpen(true);
    } catch (err) {
      console.error(err);
    }
  };

  const handleEvaluate = async (id) => {
    try {
      const res = await axios.post(`/api/landfills/${id}/evaluate`);
      setEvaluation(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  return <>
    return (
        <MapContainer
            center={[44.8176, 20.4569]}
            zoom={8}
            style={{ height: "100vh", width: "100%" }}
        >
            <TileLayer url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png" />
        </MapContainer>
        );
  </>;
}

export default LandfillMap;