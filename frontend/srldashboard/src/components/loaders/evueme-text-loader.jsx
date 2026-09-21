import { PulseLoader } from "react-spinners";

const EvuemeTextLoader = ({ text, loaderClassName = "" }) => {
  return (
    <div className="text-loader-overlay">
      <div className={`text-loader ${loaderClassName}`}>
        <p>{text}</p>
        <PulseLoader
          color="#a78540"
          loading={true}
          size={5}
          aria-label="Pulsing text loader"
        />
      </div>
    </div>
  );
};

export default EvuemeTextLoader;
