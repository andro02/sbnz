import { LuSquareDashed, LuFocus, LuShieldAlert, LuLoader, LuTruck, LuCalendar, LuBanknote, LuCircleCheck, LuCircleX, LuChevronDown, LuChevronUp, LuRefreshCw, LuFlame, LuDroplets, LuUsers, LuWaves, LuFactory, LuNavigation, LuSchool } from "react-icons/lu";
import { PiPolygonBold } from "react-icons/pi";
import { toDMS } from "../utils/toDMS";
import { useState, useEffect } from "react";

import LandfillImageCanvas from "./LandfillImageCanvas";
import InfoPanel from "./InfoPanel";

import "../css/InfoPanel.css"

// ─── constants ───────────────────────────────────────────────────────────────

const RISK_META = {
    MODERATE: { label: "Moderate Risk", color: "#c08000" },
    HIGH:     { label: "High Risk",     color: "#c04000" },
    CRITICAL: { label: "Critical Risk", color: "#9b1010" },
};

const PREREQ_LABELS = {
    TraktorDostupan:     "Tractor available",
    KamionDostupan:      "Truck available",
    PristupniPut:        "Access road",
    MinimalniBudzet:     "Minimal budget",
    SrednjiBudzet:       "Medium budget",
    VelikiBudzet:        "Large budget",
    DozvolaOpstine:      "Municipal permit",
    SpecijalizovanOtpad: "Hazmat permit",
    EkoNadzor:           "Eco supervision",
};

const RISK_BARS = [
    { key: "riverRisk",            label: "River proximity",       max: 10 },
    { key: "lakeRisk",             label: "Lake proximity",        max: 15 },
    { key: "urbanRisk",            label: "Urban proximity",       max: 15 },
    { key: "sizeRisk",             label: "Size",                  max: 10 },
    { key: "industryRisk",         label: "Industry proximity",    max:  5 },
    { key: "sensPopRisk",          label: "Sensitive population",  max:  2 },
    { key: "accessibilityPenalty", label: "Accessibility penalty", max:  5 },
    // roadRisk je 0–1 indikator pristupa, prikazuje se odvojeno kao Access possible
];

// ─── sub-components ───────────────────────────────────────────────────────────

function RiskBadge({ riskLevel, totalRisk }) {
    const meta = RISK_META[riskLevel] ?? RISK_META.MODERATE;
    return (
        <div className="eval-risk-badge" style={{ "--risk-color": meta.color }}>
            <span className="eval-risk-dot" />
            <span className="eval-risk-label">{meta.label}</span>
            <span className="eval-risk-score">{totalRisk?.toFixed(2)} pts</span>
        </div>
    );
}

function RiskBar({ label, value, max }) {
    const pct = Math.min(Math.max((value / max) * 100, 0), 100);
    if (value == null || value === 0) return null;
    return (
        <p className="eval-bar-row">
            <span className="eval-bar-label">{label}</span>
            <span className="eval-bar-wrap">
                <span className="eval-bar-track">
                    <span className="eval-bar-fill" style={{ width: `${pct}%` }} />
                </span>
                <span className="eval-bar-num">{value.toFixed(2)}</span>
            </span>
        </p>
    );
}

function PrereqChip({ name, fulfilled }) {
    return (
        <div className={`eval-prereq-chip ${fulfilled ? "ok" : "missing"}`}>
            {fulfilled
                ? <LuCircleCheck size={12} />
                : <LuCircleX size={12} />}
            <span>{PREREQ_LABELS[name] ?? name}</span>
        </div>
    );
}

function CollapsibleCard({ title, badge, defaultOpen = true, children }) {
    const [open, setOpen] = useState(defaultOpen);
    return (
        <div className="eval-card">
            <button className="eval-card-toggle" onClick={() => setOpen(o => !o)}>
                <span>{title}</span>
                <span className="eval-card-toggle-right">
                    {badge}
                    {open ? <LuChevronUp size={13} /> : <LuChevronDown size={13} />}
                </span>
            </button>
            {open && <div className="eval-card-body">{children}</div>}
        </div>
    );
}

function deduplicateByName(features) {
    const map = new Map();
    for (const f of features) {
        const existing = map.get(f.name);
        if (!existing || f.distanceM < existing.distanceM) {
            map.set(f.name, f);
        }
    }
    return Array.from(map.values());
}

function CollapsibleFlag({ icon, label, className, children }) {
    const [open, setOpen] = useState(false);
    return (
        <div className={`eval-flag ${className} collapsible`}>
            <button className="eval-flag-toggle" onClick={() => setOpen(o => !o)}>
                {icon}
                <span>{label}</span>
                {open ? <LuChevronUp size={11} /> : <LuChevronDown size={11} />}
            </button>
            {open && <div className="eval-flag-details">{children}</div>}
        </div>
    );
}

function StatusFlags({ result }) {
    const cities    = result?.nearbyFeatures?.filter(f => f.featureType === "city") ?? [];
    const rivers    = result?.nearbyFeatures?.filter(f => f.featureType === "river") ?? [];
    const lakes     = result?.nearbyFeatures?.filter(f => f.featureType === "lake") ?? [];
    const schools   = result?.nearbyFeatures?.filter(f => f.featureType === "school") ?? [];
    const roads      = deduplicateByName(result?.nearbyFeatures?.filter(f => f.featureType === "road") ?? []);
    const industries = deduplicateByName(result?.nearbyFeatures?.filter(f => f.featureType === "industrialZone") ?? []);

    const threatenedNames = new Set(result?.threatenedWaterBodies ?? []);
    const threatenedLakes = lakes.filter(l => threatenedNames.has(l.name));
    const allWaterFeatures = [
        ...rivers.map(r => ({ name: r.name, distanceM: r.distanceM, type: "river" })),
        ...threatenedLakes.map(l => ({ name: l.name, distanceM: l.distanceM, type: "lake" })),
    ];

    return (
        <div className="eval-flags">
            {result?.fireRisk && (
                <div className="eval-flag flag-fire">
                    <LuFlame size={12} />
                    <span>Fire risk</span>
                </div>
            )}

            {result?.waterRiskFlag && allWaterFeatures.length > 0 && (
                <CollapsibleFlag
                    icon={<LuWaves size={12} />}
                    label={`Water risk — ${allWaterFeatures.length} water bod${allWaterFeatures.length === 1 ? "y" : "ies"}`}
                    className="flag-water"
                >
                    {allWaterFeatures.map((w, i) => (
                        <div key={i} className="eval-flag-detail-row">
                            {w.type === "lake" ? <LuDroplets size={11} /> : <LuWaves size={11} />}
                            <span>{w.name}</span>
                            <span className="eval-flag-detail-dist">{(w.distanceM / 1000).toFixed(2)} km</span>
                        </div>
                    ))}
                </CollapsibleFlag>
            )}

            {cities.length > 0 && (
                <CollapsibleFlag
                    icon={<LuUsers size={12} />}
                    label={`${cities.length} settlement${cities.length === 1 ? "" : "s"}, ${cities.reduce((s, c) => s + c.population, 0).toLocaleString()} residents`}
                    className="flag-population"
                >
                    {cities.map((c, i) => (
                        <div key={i} className="eval-flag-detail-row">
                            <span>{c.name}</span>
                            <span className="eval-flag-detail-dist">{c.population.toLocaleString()} res.</span>
                        </div>
                    ))}
                </CollapsibleFlag>
            )}

            {roads.length > 0 && (
                <CollapsibleFlag
                    icon={<LuNavigation size={12} />}
                    label={`${roads.length} road${roads.length === 1 ? "" : "s"} nearby`}
                    className="flag-road"
                >
                    {roads.map((r, i) => (
                        <div key={i} className="eval-flag-detail-row">
                            <LuNavigation size={11} />
                            <span>{r.name}</span>
                            <span className="eval-flag-detail-dist">{(r.distanceM / 1000).toFixed(2)} km</span>
                        </div>
                    ))}
                </CollapsibleFlag>
            )}

            {industries.length > 0 && (
                <CollapsibleFlag
                    icon={<LuFactory size={12} />}
                    label={`${industries.length} industrial zone${industries.length === 1 ? "" : "s"}`}
                    className="flag-industry"
                >
                    {industries.map((iz, i) => (
                        <div key={i} className="eval-flag-detail-row">
                            <LuFactory size={11} />
                            <span>{iz.name}</span>
                            <span className="eval-flag-detail-dist">{(iz.distanceM / 1000).toFixed(2)} km</span>
                        </div>
                    ))}
                </CollapsibleFlag>
            )}

            {schools.length > 0 && (
                <CollapsibleFlag
                    icon={<LuSchool size={12} />}
                    label={`${schools.length} school${schools.length === 1 ? "" : "s"} nearby`}
                    className="flag-school"
                >
                    {schools.map((s, i) => (
                        <div key={i} className="eval-flag-detail-row">
                            <LuSchool size={11} />
                            <span>{s.name}</span>
                            <span className="eval-flag-detail-dist">{(s.distanceM / 1000).toFixed(2)} km</span>
                        </div>
                    ))}
                </CollapsibleFlag>
            )}
        </div>
    );
}

// ─── main evaluation section ──────────────────────────────────────────────────

function EvaluationSection({ landfillId }) {
    const [phase, setPhase] = useState("idle");
    const [result, setResult] = useState(null);

    useEffect(() => {
        setPhase("idle");
        setResult(null);
    }, [landfillId]);

    async function evaluate() {
        setPhase("loading");
        try {
            const res = await fetch(`/api/landfills/${landfillId}/evaluate`, { method: "POST" });
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            setResult(await res.json());
            setPhase("done");
        } catch (e) {
            console.error(e);
            setPhase("error");
        }
    }

    const risk      = result?.riskAssessment;
    const logistics = result?.logisticsOrder;
    const prereq    = result?.prerequisiteResult;

    // Build prerequisite list: prefer allPrerequisites, fall back to requiredPrerequisites
    const allNames = prereq?.allPrerequisites?.length
        ? prereq.allPrerequisites
        : (logistics?.requiredPrerequisites ?? []);
    const missing  = new Set(prereq?.missingPrerequisites ?? []);

    return (
        <div className="info-panel-details-section eval-section">

            {/* Section header */}
            <p className="eval-section-header">
                <span className="eval-section-title">
                    <LuShieldAlert size={14} />
                    Risk Assessment
                </span>
                {phase === "done" && (
                    <button className="eval-rerun-btn" title="Re-evaluate" onClick={evaluate}>
                        <LuRefreshCw size={13} />
                    </button>
                )}
            </p>

            {/* Idle */}
            {phase === "idle" && (
                <button className="eval-trigger-btn" onClick={evaluate}>
                    Evaluate Risk
                </button>
            )}

            {/* Loading */}
            {phase === "loading" && (
                <div className="eval-loading">
                    <LuLoader className="eval-spinner" size={14} />
                    <span>Running Drools evaluation…</span>
                </div>
            )}

            {/* Error */}
            {phase === "error" && (
                <p className="eval-error">
                    Evaluation failed.
                    <button onClick={evaluate}>Retry</button>
                </p>
            )}

            {/* Results */}
            {phase === "done" && risk && (
                <div className="eval-results">

                    <RiskBadge riskLevel={risk.riskLevel} totalRisk={risk.totalRisk} />

                    <StatusFlags result={result} />

                    {/* Risk breakdown */}
                    <CollapsibleCard title="Risk Breakdown">
                        {RISK_BARS.map(({ key, label, max }) =>
                            risk[key] != null
                                ? <RiskBar key={key} label={label} value={risk[key]} max={max} />
                                : null
                        )}
                    </CollapsibleCard>

                    {/* Logistics order */}
                    {logistics && (
                        <CollapsibleCard title="Logistics Order">
                            <p>
                                <span className="eval-logistics-icon-label">
                                    <LuTruck size={13} /> Equipment
                                </span>
                                <span>{logistics.mehanization}</span>
                            </p>
                            <p>
                                <span className="eval-logistics-icon-label">
                                    <LuCalendar size={13} /> Deadline
                                </span>
                                <span>{logistics.deadlineDays} days</span>
                            </p>
                            <p>
                                <span className="eval-logistics-icon-label">
                                    <LuBanknote size={13} /> Budget
                                </span>
                                <span>{logistics.budgetCategory}</span>
                            </p>
                            <p>
                                <span className="eval-logistics-icon-label">
                                    <LuRefreshCw size={13} /> Road access
                                </span>
                                <span className={result?.accessPossible ? "eval-access-yes" : "eval-access-no"}>
                                    {result?.accessPossible ? "Accessible" : "No access"}
                                </span>
                            </p>
                        </CollapsibleCard>
                    )}

                    {/* Prerequisites */}
                    {allNames.length > 0 && (
                        <CollapsibleCard
                            title="Prerequisites"
                            badge={
                                <span className={`eval-prereq-badge ${prereq?.readyForSanation ? "ready" : "not-ready"}`}>
                                    {prereq?.readyForSanation
                                        ? "Ready"
                                        : `${missing.size} missing`}
                                </span>
                            }
                        >
                            <div className="eval-prereq-chips">
                                {allNames.map(name => (
                                    <PrereqChip key={name} name={name} fulfilled={!missing.has(name)} />
                                ))}
                            </div>
                        </CollapsibleCard>
                    )}
                </div>
            )}
        </div>
    );
}

// ─── main component ───────────────────────────────────────────────────────────

function LandfillInfoPanel({ open, onClose, landfill }) {
    const [showBoundingBox, setShowBoundingBox] = useState(false);
    const [showBoundingPolygon, setShowBoundingPolygon] = useState(true);
    const [enableZoom, setEnableZoom] = useState(false);

    const formatter = new Intl.NumberFormat("en-US", { minimumFractionDigits: 0, maximumFractionDigits: 2 });

    if (!landfill) return;

    return (
        <InfoPanel title="Serbia Landfill Overview" open={open} onClose={onClose}
            actions={
                <>
                    {landfill?.source === "detected" && landfill?.status != "Sanitary" && (
                        <>
                            <button className={`panel-btn bbox ${showBoundingBox ? "on" : ""}`}
                                title="Show bounding box" onClick={() => setShowBoundingBox(!showBoundingBox)}>
                                <LuSquareDashed />
                            </button>
                            <button className={`panel-btn seg ${showBoundingPolygon ? "on" : ""}`}
                                title="Show bounding polygon" onClick={() => setShowBoundingPolygon(!showBoundingPolygon)}>
                                <PiPolygonBold />
                            </button>
                        </>
                    )}
                    <button className={`panel-btn zoom ${enableZoom ? "on" : ""} ${landfill.source !== "detected" || landfill.status == "Sanitary" ? "registry" : ""}`}
                        title="Enable zoom" onClick={() => setEnableZoom(!enableZoom)}>
                        <LuFocus />
                    </button>
                </>
            }
        >
            {landfill && (
                <LandfillImageCanvas
                    className="info-panel-image"
                    landfill={landfill}
                    showBoundingBox={showBoundingBox}
                    showBoundingPolygon={showBoundingPolygon}
                    enableZoom={enableZoom}
                />
            )}

            <div className="info-panel-details" id="landfill">
                <div className="info-panel-details-section">
                    <h2>Landfill #{landfill.id}</h2>
                    <p>{toDMS(landfill.centerLat, true)} {toDMS(landfill.centerLon, false)}</p>
                    <h4>{`${landfill.status} Landfill`.toUpperCase()}</h4>
                </div>

                <div className="info-panel-details-section">
                    <p>Estimated area<span>{formatter.format(landfill.areaM2)} m²</span></p>
                    <p>Estimated volume<span>{formatter.format(landfill.volumeM3)} m³</span></p>
                </div>

                <div className="info-panel-details-section">
                    <p>Estimated CH₄ emissions<span>{formatter.format(landfill.annualCh4Tonnes)} ton/year</span></p>
                    <p>Estimated CH₄ emissions (CO₂eq)<span>{formatter.format(landfill.annualCo2eTonnes)} ton/year</span></p>
                </div>

                <EvaluationSection landfillId={landfill.id} />
            </div>
        </InfoPanel>
    );
}

export default LandfillInfoPanel;
