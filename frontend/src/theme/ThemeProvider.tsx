import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";

export const BACKGROUNDS = [
  { id: "study", label: "Study space", image: "https://images.unsplash.com/photo-1787471953581-e56ac87741d0?crop=entropy&cs=srgb&fm=jpg&ixid=M3wzMjM4NDZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE3ODg2MjQwODF8&ixlib=rb-4.1.0&q=85" },
  { id: "forest", label: "Forest", image: "https://images.unsplash.com/photo-1787401270516-ea5bf43088ee?crop=entropy&cs=srgb&fm=jpg&ixid=M3wzMjM4NDZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE3ODg2MjQwODF8&ixlib=rb-4.1.0&q=85" },
  { id: "coast", label: "Coast", image: "https://images.unsplash.com/photo-1785845333757-cbbd4fc224af?crop=entropy&cs=srgb&fm=jpg&ixid=M3wzMjM4NDZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE3ODg2MjQwODF8&ixlib=rb-4.1.0&q=85" },
  { id: "mountain", label: "Mountain", image: "https://images.unsplash.com/photo-1786658054226-c9c2456d4294?crop=entropy&cs=srgb&fm=jpg&ixid=M3wzMjM4NDZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE3ODg2MjQwODF8&ixlib=rb-4.1.0&q=85" }
] as const;

export const FONTS = [
  { id: "raleway", label: "Raleway", body: "Raleway, Arial, sans-serif", heading: "Montserrat, Raleway, sans-serif" },
  { id: "inconsolata", label: "Inconsolata", body: "Inconsolata, monospace", heading: "Inconsolata, monospace" },
  { id: "lora", label: "Lora", body: "Lora, Georgia, serif", heading: "Lora, Georgia, serif" },
  { id: "montserrat", label: "Montserrat", body: "Montserrat, Arial, sans-serif", heading: "Montserrat, Arial, sans-serif" },
  { id: "roboto", label: "Roboto", body: "Roboto, Arial, sans-serif", heading: "Roboto, Arial, sans-serif" },
  { id: "merriweather", label: "Merriweather", body: "Merriweather, Georgia, serif", heading: "Merriweather, Georgia, serif" }
] as const;

type BackgroundId = (typeof BACKGROUNDS)[number]["id"];
type FontId = (typeof FONTS)[number]["id"];

interface Appearance {
  backgroundId: BackgroundId;
  fontId: FontId;
  brightness: number;
  contrast: number;
}

interface ThemeContextValue extends Appearance {
  setBackgroundId: (value: BackgroundId) => void;
  setFontId: (value: FontId) => void;
  setBrightness: (value: number) => void;
  setContrast: (value: number) => void;
  resetAppearance: () => void;
}

const DEFAULT_APPEARANCE: Appearance = { backgroundId: "study", fontId: "raleway", brightness: 100, contrast: 105 };
const STORAGE_KEY = "tasktrek.appearance";
const ThemeContext = createContext<ThemeContextValue | null>(null);

function readAppearance(): Appearance {
  try {
    const stored = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? "{}") as Partial<Appearance>;
    const backgroundId = BACKGROUNDS.some((item) => item.id === stored.backgroundId) ? stored.backgroundId as BackgroundId : DEFAULT_APPEARANCE.backgroundId;
    const fontId = FONTS.some((item) => item.id === stored.fontId) ? stored.fontId as FontId : DEFAULT_APPEARANCE.fontId;
    return {
      backgroundId,
      fontId,
      brightness: typeof stored.brightness === "number" ? Math.max(35, Math.min(130, stored.brightness)) : DEFAULT_APPEARANCE.brightness,
      contrast: typeof stored.contrast === "number" ? Math.max(75, Math.min(150, stored.contrast)) : DEFAULT_APPEARANCE.contrast
    };
  } catch { return DEFAULT_APPEARANCE; }
}

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [appearance, setAppearance] = useState<Appearance>(readAppearance);

  useEffect(() => {
    const background = BACKGROUNDS.find((item) => item.id === appearance.backgroundId)!;
    const font = FONTS.find((item) => item.id === appearance.fontId)!;
    const root = document.documentElement;
    root.style.setProperty("--background-image", `url("${background.image}")`);
    root.style.setProperty("--background-brightness", `${appearance.brightness}%`);
    root.style.setProperty("--background-contrast", `${appearance.contrast}%`);
    root.style.setProperty("--body-font", font.body);
    root.style.setProperty("--heading-font", font.heading);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(appearance));
  }, [appearance]);

  const value = useMemo<ThemeContextValue>(() => ({
    ...appearance,
    setBackgroundId: (backgroundId) => setAppearance((current) => ({ ...current, backgroundId })),
    setFontId: (fontId) => setAppearance((current) => ({ ...current, fontId })),
    setBrightness: (brightness) => setAppearance((current) => ({ ...current, brightness })),
    setContrast: (contrast) => setAppearance((current) => ({ ...current, contrast })),
    resetAppearance: () => setAppearance(DEFAULT_APPEARANCE)
  }), [appearance]);

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  const value = useContext(ThemeContext);
  if (!value) throw new Error("useTheme must be used inside ThemeProvider");
  return value;
}
